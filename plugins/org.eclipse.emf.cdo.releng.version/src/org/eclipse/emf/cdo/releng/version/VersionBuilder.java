/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

import org.osgi.framework.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class VersionBuilder extends IncrementalProjectBuilder implements ElementResolver
{
  public static final String BUILDER_ID = "org.eclipse.emf.cdo.releng.version.VersionBuilder";

  public static final String RELEASE_PATH_ARGUMENT = "release.path";

  public static final String VALIDATOR_CLASS_ARGUMENT = "validator.class";

  public static final String IGNORE_DEPENDENCY_RANGES_ARGUMENT = "ignore.missing.dependency.ranges";

  public static final String IGNORE_EXPORT_VERSIONS_ARGUMENT = "ignore.missing.export.versions";

  public static final String IGNORE_CONTENT_CHANGES_ARGUMENT = "ignore.feature.content.changes";

  private static final Path MANIFEST_PATH = new Path("META-INF/MANIFEST.MF");

  private static final Path FEATURE_PATH = new Path("feature.xml");

  private static final Version ADDITION = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

  private static final Version REMOVAL = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

  private static final int NO_CHANGE = 0;

  private static final int MICRO_CHANGE = 1;

  private static final int MINOR_CHANGE = 2;

  private static final int MAJOR_CHANGE = 3;

  private static final boolean DEBUG = false;

  private Release release;

  public VersionBuilder()
  {
  }

  public Element resolveElement(Element key)
  {
    try
    {
      IModel componentModel = ReleaseManager.INSTANCE.getComponentModel(key);
      if (componentModel != null)
      {
        Element element = ReleaseManager.INSTANCE.createElement(componentModel, true);
        if (element.getType() == Element.Type.FEATURE)
        {
          fillChildren(componentModel, element);
        }

        return element;
      }
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }

    return null;
  }

  @SuppressWarnings("restriction")
  private List<Element> fillChildren(IModel componentModel, Element element)
  {
    org.eclipse.pde.internal.core.ifeature.IFeatureModel featureModel = (org.eclipse.pde.internal.core.ifeature.IFeatureModel)componentModel;
    org.eclipse.pde.internal.core.ifeature.IFeature feature = featureModel.getFeature();

    List<Element> children = element.getChildren();
    for (org.eclipse.pde.internal.core.ifeature.IFeatureChild versionable : feature.getIncludedFeatures())
    {
      Element childElement = new Element(Element.Type.FEATURE, versionable.getId(), versionable.getVersion());
      children.add(childElement);
    }

    for (org.eclipse.pde.internal.core.ifeature.IFeaturePlugin versionable : feature.getPlugins())
    {
      Element childElement = new Element(Element.Type.PLUGIN, versionable.getId(), versionable.getVersion());
      children.add(childElement);
    }

    return children;
  }

  @Override
  protected void clean(IProgressMonitor monitor) throws CoreException
  {
    monitor.beginTask(null, 1);

    try
    {
      IProject project = getProject();
      Markers.deleteAllMarkers(project);
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected final IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
      throws CoreException
  {
    IProject project = getProject();
    List<IProject> buildDpependencies = new ArrayList<IProject>();

    BuildState buildState = Activator.getBuildState(project);
    boolean fullBuild = buildState.getReleaseTag() == null;
    VersionValidator validator = null;

    monitor.beginTask(null, 1);

    try
    {
      Markers.deleteAllMarkers(project);

      IModel componentModel = getComponentModel(project);
      IFile projectDescription = project.getFile(new Path(".project"));

      /*
       * Determine release data to validate against
       */

      String releasePath = (String)args.get(RELEASE_PATH_ARGUMENT);
      if (releasePath == null)
      {
        String msg = "Build command argument missing: " + RELEASE_PATH_ARGUMENT;
        Markers.addMarker(projectDescription, msg, IMarker.SEVERITY_ERROR, "(" + BUILDER_ID + ")");
        return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
      }

      try
      {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(releasePath));
        buildDpependencies.add(file.getProject());

        Release release;
        if (!file.exists())
        {
          release = ReleaseManager.INSTANCE.createRelease(file);
        }
        else
        {
          release = ReleaseManager.INSTANCE.getRelease(file);
        }

        boolean releaseHasChanged = !release.getTag().equals(buildState.getReleaseTag());
        if (releaseHasChanged)
        {
          buildState.setReleaseTag(release.getTag());
          fullBuild = true;
        }

        this.release = release;
      }
      catch (CoreException ex)
      {
        ex.printStackTrace();
        String msg = "Problem with release spec: " + releasePath;
        Markers.addMarker(projectDescription, msg, IMarker.SEVERITY_ERROR, "(" + releasePath.replace(".", "\\.") + ")");
        return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
      }

      /*
       * Determine if a validation is needed or if the version has already been increased properly
       */

      Element element = ReleaseManager.INSTANCE.createElement(componentModel);
      Element releaseElement = release.getElements().get(element);
      if (releaseElement == null)
      {
        trace("Project has not been released: " + project.getName());
        return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
      }

      Version elementVersion = element.getVersion();
      Version releaseVersion = releaseElement.getVersion();
      Version nextImplVersion = new Version(releaseVersion.getMajor(), releaseVersion.getMinor(),
          releaseVersion.getMicro() + (release.isIntegration() ? 100 : 1));
      int comparison = releaseVersion.compareTo(elementVersion);
      if (comparison < 0)
      {
        if (!nextImplVersion.equals(elementVersion))
        {
          if (elementVersion.getMajor() == nextImplVersion.getMajor()
              && elementVersion.getMinor() == nextImplVersion.getMinor())
          {
            addVersionMarker("Version should be " + nextImplVersion);
          }
        }

        if (componentModel instanceof IPluginModelBase)
        {
          return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
        }
      }

      if (comparison > 0)
      {
        addVersionMarker("Version has been decreased after release " + releaseVersion);
        return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
      }

      if (componentModel instanceof IPluginModelBase)
      {
        if (!"true".equals(args.get(IGNORE_DEPENDENCY_RANGES_ARGUMENT)))
        {
          checkDependencyRanges((IPluginModelBase)componentModel);
        }

        if (!"true".equals(args.get(IGNORE_EXPORT_VERSIONS_ARGUMENT)))
        {
          checkPackageExports((IPluginModelBase)componentModel);
        }
      }
      else if (!"true".equals(args.get(IGNORE_CONTENT_CHANGES_ARGUMENT)))
      {
        List<Map.Entry<Element, Version>> warnings = new ArrayList<Entry<Element, Version>>();
        int change = checkFeatureAPI(componentModel, element, releaseElement, buildDpependencies, warnings);
        if (change != NO_CHANGE)
        {
          Version nextFeatureVersion = null;
          if (change == MAJOR_CHANGE)
          {
            nextFeatureVersion = new Version(releaseVersion.getMajor() + 1, 0, 0);
          }
          else if (change == MINOR_CHANGE)
          {
            nextFeatureVersion = new Version(releaseVersion.getMajor(), releaseVersion.getMinor() + 1, 0);
          }
          else if (change == MICRO_CHANGE)
          {
            nextFeatureVersion = nextImplVersion;
          }

          if (elementVersion.compareTo(nextFeatureVersion) < 0)
          {
            addVersionMarker("Version must be increased to " + nextFeatureVersion
                + " because the feature's references have changed");

            for (Entry<Element, Version> entry : warnings)
            {
              addIncludeMarker(entry.getKey(), entry.getValue());
            }
          }

          return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
        }

        if (!elementVersion.equals(releaseVersion))
        {
          return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
        }
      }

      /*
       * Determine validator to use
       */

      String validatorClass = (String)args.get(VALIDATOR_CLASS_ARGUMENT);
      if (validatorClass == null)
      {
        validatorClass = "org.eclipse.emf.cdo.releng.version.digest.DigestValidator$BuildModel";
      }

      try
      {
        Class<?> c = Class.forName(validatorClass, true, VersionBuilder.class.getClassLoader());
        validator = (VersionValidator)c.newInstance();
      }
      catch (Exception ex)
      {
        String msg = ex.getLocalizedMessage() + ": " + validatorClass;
        Markers.addMarker(projectDescription, msg, IMarker.SEVERITY_ERROR, ".*(" + validatorClass + ").*");
        return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
      }

      trace(validator.getClass().getName() + ": " + project.getName());

      /*
       * Do the validation
       */

      IResourceDelta delta = null;
      fullBuild |= kind == FULL_BUILD;
      if (!fullBuild)
      {
        delta = getDelta(project);
      }

      validator.updateBuildState(buildState, releasePath, release, project, delta, componentModel, monitor);

      try
      {
        if (buildState.isChangedSinceRelease())
        {
          addVersionMarker("Version must be increased to " + nextImplVersion
              + " because the project's contents have changed");
        }
      }
      catch (Exception ignore)
      {
        Activator.log(ignore);
      }
    }
    catch (Exception ex)
    {
      try
      {
        if (validator != null)
        {
          validator.abort(buildState, project, ex, monitor);
        }

        String msg = Activator.log(ex);
        Markers.addMarker(project, msg);
      }
      catch (Exception ignore)
      {
        Activator.log(ignore);
      }
    }
    finally
    {
      monitor.done();
    }

    return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
  }

  private int checkFeatureAPI(IModel componentModel, Element element, Element releasedElement,
      List<IProject> buildDpependencies, List<Entry<Element, Version>> warnings)
  {
    List<Element> children = fillChildren(componentModel, element);
    for (Element pluginChild : children)
    {
      if (pluginChild.getType() == Element.Type.PLUGIN)
      {
        for (Element featureChild : children)
        {
          if (featureChild.getType() == Element.Type.FEATURE)
          {
            featureChild = resolveElement(featureChild);
            if (featureChild != null)
            {
              Set<Element> allChildren = featureChild.getAllChildren(this);
              if (allChildren.contains(pluginChild))
              {
                try
                {
                  addRedundancyMarker(pluginChild, featureChild);
                }
                catch (Exception ex)
                {
                  Activator.log(ex);
                }
              }
            }
          }
        }
      }
    }

    int biggestChange = NO_CHANGE;
    Set<Element> allChildren = element.getAllChildren(this);
    for (Element child : allChildren)
    {
      int change = checkFeatureAPI(element, releasedElement, child, warnings);
      biggestChange = Math.max(biggestChange, change);

      IProject project = getProject(child);
      if (project != null)
      {
        buildDpependencies.add(project);
      }
    }

    for (Element releasedElementsChild : releasedElement.getAllChildren(release))
    {
      if (!allChildren.contains(releasedElementsChild))
      {
        addWarning(releasedElementsChild, REMOVAL, warnings);
        biggestChange = MAJOR_CHANGE; // REMOVAL
      }
    }

    return biggestChange;
  }

  private int checkFeatureAPI(Element element, Element releasedElement, Element childElement,
      List<Entry<Element, Version>> warnings)
  {
    if (childElement.isUnresolved())
    {
      return NO_CHANGE;
    }

    Element releasedElementsChild = releasedElement.getChild(release, childElement);
    if (releasedElementsChild == null)
    {
      addWarning(childElement, ADDITION, warnings);
      return MINOR_CHANGE; // ADDITION
    }

    Element childsReleasedElement = release.getElements().get(childElement);
    if (childsReleasedElement == null)
    {
      return NO_CHANGE;
    }

    Version releasedVersion = childsReleasedElement.getVersion();
    Version version = childElement.getVersion();
    if (version == null)
    {
      return NO_CHANGE;
    }

    if (version.getMajor() != releasedVersion.getMajor())
    {
      addWarning(childsReleasedElement, version, warnings);
      return MAJOR_CHANGE;
    }

    if (version.getMinor() != releasedVersion.getMinor())
    {
      addWarning(childsReleasedElement, version, warnings);
      return MINOR_CHANGE;
    }

    if (version.getMicro() != releasedVersion.getMicro())
    {
      addWarning(childsReleasedElement, version, warnings);
      return MICRO_CHANGE;
    }

    return NO_CHANGE;
  }

  private void addWarning(final Element releasedElement, final Version version,
      List<Map.Entry<Element, Version>> warnings)
  {
    warnings.add(new Map.Entry<Element, Version>()
    {
      public Element getKey()
      {
        return releasedElement;
      }

      public Version getValue()
      {
        return version;
      }

      public Version setValue(Version value)
      {
        throw new UnsupportedOperationException();
      }
    });
  }

  private IProject getProject(Element element)
  {
    String name = element.getName();
    if (element.getType() == Element.Type.PLUGIN)
    {
      return getPluginProject(name);
    }

    return getFeatureProject(name);
  }

  private IProject getPluginProject(String name)
  {
    IPluginModelBase pluginModel = PluginRegistry.findModel(name);
    if (pluginModel != null)
    {
      IResource resource = pluginModel.getUnderlyingResource();
      if (resource != null)
      {
        return resource.getProject();
      }
    }

    return null;
  }

  @SuppressWarnings("restriction")
  private IProject getFeatureProject(String name)
  {
    org.eclipse.pde.internal.core.ifeature.IFeatureModel[] featureModels = org.eclipse.pde.internal.core.PDECore
        .getDefault().getFeatureModelManager().getWorkspaceModels();

    for (org.eclipse.pde.internal.core.ifeature.IFeatureModel featureModel : featureModels)
    {
      IResource resource = featureModel.getUnderlyingResource();
      if (resource != null && featureModel.getFeature().getId().equals(name))
      {
        return resource.getProject();
      }
    }

    return null;
  }

  private void checkDependencyRanges(IPluginModelBase pluginModel) throws CoreException, IOException
  {
    BundleDescription description = pluginModel.getBundleDescription();
    if (description == null)
    {
      return;
    }

    for (BundleSpecification requiredBundle : description.getRequiredBundles())
    {
      VersionRange range = requiredBundle.getVersionRange();
      if (isUnspecified(getMaximum(range)))
      {
        addRequireMarker(requiredBundle.getName(), "dependency must specify a version range");
      }
      else
      {
        if (!range.getIncludeMinimum())
        {
          addRequireMarker(requiredBundle.getName(), "dependency range must include the minimum");
        }

        if (range.getIncludeMaximum())
        {
          addRequireMarker(requiredBundle.getName(), "dependency range must not include the maximum");
        }
      }
    }

    for (ImportPackageSpecification importPackage : description.getImportPackages())
    {
      VersionRange range = importPackage.getVersionRange();
      if (isUnspecified(getMaximum(range)))
      {
        addImportMarker(importPackage.getName(), "dependency must specify a version range");
      }
      else
      {
        if (!range.getIncludeMinimum())
        {
          addImportMarker(importPackage.getName(), "dependency range must include the minimum");
        }

        if (range.getIncludeMaximum())
        {
          addImportMarker(importPackage.getName(), "dependency range must not include the maximum");
        }
      }
    }
  }

  @SuppressWarnings("deprecation")
  private Version getMaximum(VersionRange range)
  {
    VersionUtil.someDeprecatedCode(); // Just make sure that this method refers to some deprecated code
    return range.getMaximum();
  }

  private boolean isUnspecified(Version version)
  {
    if (version.getMajor() != Integer.MAX_VALUE)
    {
      return false;
    }

    if (version.getMinor() != Integer.MAX_VALUE)
    {
      return false;
    }

    if (version.getMicro() != Integer.MAX_VALUE)
    {
      return false;
    }

    return true;
  }

  private void checkPackageExports(IPluginModelBase pluginModel) throws CoreException, IOException
  {
    BundleDescription description = pluginModel.getBundleDescription();
    String bundleName = description.getSymbolicName();
    Version bundleVersion = VersionUtil.normalize(description.getVersion());

    for (ExportPackageDescription packageExport : description.getExportPackages())
    {
      String packageName = packageExport.getName();
      if (isBundlePackage(packageName, bundleName))
      {
        Version packageVersion = packageExport.getVersion();
        if (packageVersion != null && !packageVersion.equals(Version.emptyVersion)
            && !packageVersion.equals(bundleVersion))
        {
          addExportMarker(packageName);
        }
      }
    }
  }

  private boolean isBundlePackage(String packageName, String bundleName)
  {
    if (packageName.startsWith(bundleName))
    {
      return true;
    }

    int lastDot = bundleName.lastIndexOf('.');
    if (lastDot != -1)
    {
      String bundleStart = bundleName.substring(0, lastDot);
      String bundleEnd = bundleName.substring(lastDot + 1);
      if (packageName.startsWith(bundleStart + ".internal." + bundleEnd))
      {
        return true;
      }

      if (packageName.startsWith(bundleStart + ".spi." + bundleEnd))
      {
        return true;
      }
    }

    return false;
  }

  private void addRequireMarker(String name, String message)
  {
    try
    {
      IFile file = getProject().getFile(MANIFEST_PATH);
      String regex = name.replaceAll("\\.", "\\\\.") + ";bundle-version=\"([^\\\"]*)\"";
      Markers.addMarker(file, "'" + name + "' " + message, IMarker.SEVERITY_ERROR, regex);
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private void addImportMarker(String name, String message)
  {
    try
    {
      IFile file = getProject().getFile(MANIFEST_PATH);
      String regex = name.replaceAll("\\.", "\\\\.") + ";version=\"([^\\\"]*)\"";
      Markers.addMarker(file, "'" + name + "' " + message, IMarker.SEVERITY_ERROR, regex);
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private void addExportMarker(String name)
  {
    try
    {
      IFile file = getProject().getFile(MANIFEST_PATH);
      String message = "'" + name + "' export has wrong version information";
      String regex = name.replaceAll("\\.", "\\\\.") + ";version=\"([0123456789\\.]*)\"";
      Markers.addMarker(file, message, IMarker.SEVERITY_ERROR, regex);
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private void addVersionMarker(String message)
  {
    try
    {
      String regex;
      IFile file = getProject().getFile(FEATURE_PATH);
      if (file.exists())
      {
        regex = "feature.*?version\\s*=\\s*[\"'](\\d+(\\.\\d+(\\.\\d+)?)?)";
      }
      else
      {
        file = getProject().getFile(MANIFEST_PATH);
        regex = "Bundle-Version: *(\\d+(\\.\\d+(\\.\\d+)?)?)";
      }

      Markers.addMarker(file, message, IMarker.SEVERITY_ERROR, regex);
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private void addRedundancyMarker(Element pluginChild, Element featureChild)
  {
    try
    {
      IFile file = getProject().getFile(FEATURE_PATH);
      String name = pluginChild.getName();
      String msg = "Plug-in reference '" + name + "' is redundant because feature '" + featureChild.getName()
          + "' already includes it";
      addFeatureChildMarker(file, "plugin", name, msg);
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private void addIncludeMarker(Element releasedElement, Version version)
  {
    try
    {
      IFile file = getProject().getFile(FEATURE_PATH);
      String type;
      String tag;
      if (releasedElement.getType() == Element.Type.PLUGIN)
      {
        type = "Plug-in";
        tag = "plugin";
      }
      else
      {
        type = "Feature";
        tag = "includes";
      }

      String name = releasedElement.getName();

      if (version == REMOVAL)
      {
        String msg = type + " reference '" + name + "' has been removed";
        Markers.addMarker(file, msg, IMarker.SEVERITY_WARNING);
      }
      else
      {
        String msg;
        if (version == ADDITION)
        {
          msg = type + " reference '" + name + "' has been added with " + releasedElement.getVersion();
        }
        else
        {
          msg = type + " reference '" + name + "' has been changed from " + releasedElement.getVersion() + " to "
              + version;
        }

        addFeatureChildMarker(file, tag, name, msg);
      }
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private void addFeatureChildMarker(IFile file, String tag, String name, String msg) throws CoreException, IOException
  {
    String regex = "<" + tag + "\\s+.*?id\\s*=\\s*[\"'](" + name.replace(".", "\\.") + ")";
    Markers.addMarker(file, msg, IMarker.SEVERITY_WARNING, regex);
  }

  public static void trace(String msg)
  {
    if (DEBUG)
    {
      System.out.println(msg);
    }
  }

  public static IModel getComponentModel(IProject project)
  {
    IModel componentModel = PluginRegistry.findModel(project);
    if (componentModel == null)
    {
      componentModel = getFeatureModel(project);
      if (componentModel == null)
      {
        throw new IllegalStateException("The project " + project.getName() + " is neither a plugin nor a feature");
      }
    }

    return componentModel;
  }

  @SuppressWarnings("restriction")
  private static org.eclipse.pde.internal.core.ifeature.IFeatureModel getFeatureModel(IProject project)
  {
    org.eclipse.pde.internal.core.ifeature.IFeatureModel[] featureModels = org.eclipse.pde.internal.core.PDECore
        .getDefault().getFeatureModelManager().getWorkspaceModels();

    for (org.eclipse.pde.internal.core.ifeature.IFeatureModel featureModel : featureModels)
    {
      if (featureModel.getUnderlyingResource().getProject() == project)
      {
        return featureModel;
      }
    }

    return null;
  }

  @SuppressWarnings("restriction")
  public static Version getComponentVersion(IModel componentModel)
  {
    if (componentModel instanceof IPluginModelBase)
    {
      IPluginModelBase pluginModel = (IPluginModelBase)componentModel;
      return VersionUtil.normalize(pluginModel.getBundleDescription().getVersion());
    }

    Version version = new Version(((org.eclipse.pde.internal.core.ifeature.IFeatureModel)componentModel).getFeature()
        .getVersion());
    return VersionUtil.normalize(version);
  }
}
