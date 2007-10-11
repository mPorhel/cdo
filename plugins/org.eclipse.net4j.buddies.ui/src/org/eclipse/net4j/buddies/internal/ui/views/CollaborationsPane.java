/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.buddies.IBuddyCollaboration;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.ui.bundle.OM;
import org.eclipse.net4j.buddies.protocol.ICollaboration;
import org.eclipse.net4j.buddies.protocol.IFacility;
import org.eclipse.net4j.buddies.protocol.IFacilityInstalledEvent;
import org.eclipse.net4j.buddies.ui.IFacilityPaneCreator;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerEventVisitor;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.actions.SafeAction;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CollaborationsPane extends Composite implements IListener
{
  private CollaborationsView collaborationsView;

  private ISelectionChangedListener collaborationsViewerListener = new ISelectionChangedListener()
  {
    public void selectionChanged(SelectionChangedEvent event)
    {
    }
  };

  private IBuddySession session;

  private IBuddyCollaboration activeCollaboration;

  private Map<IBuddyCollaboration, IFacility> activeFacilities = new HashMap<IBuddyCollaboration, IFacility>();

  private Map<IFacility, FacilityPane> facilityPanes = new HashMap<IFacility, FacilityPane>();

  private List<IAction> activateFacilityActions = new ArrayList<IAction>();

  private StackLayout paneStack;

  public CollaborationsPane(Composite parent, CollaborationsView collaborationsView)
  {
    super(parent, SWT.NONE);
    setLayout(paneStack = new StackLayout());

    this.collaborationsView = collaborationsView;
    collaborationsView.getViewer().addSelectionChangedListener(collaborationsViewerListener);
  }

  @Override
  public void dispose()
  {
    collaborationsView.getViewer().removeSelectionChangedListener(collaborationsViewerListener);
    super.dispose();
  }

  public CollaborationsView getCollaborationsView()
  {
    return collaborationsView;
  }

  public IBuddySession getSession()
  {
    return session;
  }

  public void setSession(IBuddySession session)
  {
    this.session = session;
    if (session != null)
    {
      for (ICollaboration collaboration : session.getSelf().getCollaborations())
      {
        collaborationAdded((IBuddyCollaboration)collaboration);
      }
    }
    else
    {
    }
  }

  public void setActiveCollaboration(IBuddyCollaboration collaboration)
  {
    if (activeCollaboration != collaboration)
    {
      activeCollaboration = collaboration;
      IFacility facility = activeFacilities.get(collaboration);
      if (facility != null)
      {
        setActiveFacility(collaboration, facility);
      }
    }
  }

  public void setActiveFacility(IBuddyCollaboration collaboration, IFacility facility)
  {
    activeFacilities.put(collaboration, facility);
    if (collaboration == activeCollaboration)
    {
      FacilityPane facilityPane = facilityPanes.get(facility);
      setActiveFacilityPane(facilityPane);
    }
  }

  protected void setActiveFacilityPane(FacilityPane newPane)
  {
    if (paneStack.topControl != newPane)
    {
      FacilityPane oldPane = (FacilityPane)paneStack.topControl;
      if (oldPane != null)
      {
        oldPane.hidden(newPane);
      }

      paneStack.topControl = newPane;
      layout();
      if (newPane != null)
      {
        newPane.showed(oldPane);
      }
    }
  }

  public void fillActionBars(IActionBars bars)
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, OM.EXT_POINT);
    for (final IConfigurationElement element : elements)
    {
      if ("facilityPaneCreator".equals(element.getName()))
      {
        String type = element.getAttribute("type");
        String icon = element.getAttribute("icon");
        ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(OM.BUNDLE_ID, icon);
        IAction action = new ActivateFacilityAction(type, descriptor);
        activateFacilityActions.add(action);
        bars.getToolBarManager().add(action);
      }
    }
  }

  public void notifyEvent(IEvent event)
  {
    System.out.println("EVENT: " + event);
    if (session != null && event.getSource() == session.getSelf() && event instanceof IContainerEvent)
    {
      IContainerEvent<ICollaboration> e = (IContainerEvent<ICollaboration>)event;
      e.accept(new IContainerEventVisitor<ICollaboration>()
      {
        public void added(ICollaboration collaboration)
        {
          collaborationAdded((IBuddyCollaboration)collaboration);
        }

        public void removed(ICollaboration collaboration)
        {
          collaborationRemoved((IBuddyCollaboration)collaboration);
        }
      });
    }
    else if (event instanceof IFacilityInstalledEvent)
    {
      IFacilityInstalledEvent e = (IFacilityInstalledEvent)event;
      facilityInstalled(e.getFacility(), e.fromRemote());
    }
  }

  protected void collaborationAdded(IBuddyCollaboration collaboration)
  {
    IFacility[] facilities = collaboration.getFacilities();
    for (IFacility facility : facilities)
    {
      addFacilityPane(facility);
    }

    if (activeCollaboration == null)
    {
      setActiveCollaboration(collaboration);
    }

    if (facilities.length != 0)
    {
      setActiveFacility(collaboration, facilities[0]);
    }

    collaboration.addListener(this);
  }

  protected void collaborationRemoved(IBuddyCollaboration collaboration)
  {
    collaboration.removeListener(this);
  }

  protected void facilityInstalled(final IFacility facility, boolean fromRemote)
  {
    final IBuddyCollaboration collaboration = (IBuddyCollaboration)facility.getCollaboration();
    if (fromRemote)
    {
      try
      {
        getDisplay().syncExec(new Runnable()
        {
          public void run()
          {
            try
            {
              addFacilityPane(facility);
              IFacility activeFacility = activeFacilities.get(collaboration);
              if (activeFacility == null)
              {
                setActiveFacility(collaboration, facility);
              }
            }
            catch (RuntimeException ignore)
            {
            }
          }
        });
      }
      catch (RuntimeException ignore)
      {
      }
    }
    else
    {
      addFacilityPane(facility);
      setActiveCollaboration(collaboration);
      setActiveFacility(collaboration, facility);
    }
  }

  protected FacilityPane addFacilityPane(IFacility facility)
  {
    IFacilityPaneCreator creator = getFacilityPaneCreator(facility.getType());
    FacilityPane pane = creator.createFacilityPane(this, SWT.NONE);
    facilityPanes.put(facility, pane);
    return pane;
  }

  protected IFacilityPaneCreator getFacilityPaneCreator(String type)
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, OM.EXT_POINT);
    for (final IConfigurationElement element : elements)
    {
      if ("facilityPaneCreator".equals(element.getName()))
      {
        if (ObjectUtil.equals(element.getAttribute("type"), type))
        {
          try
          {
            return (IFacilityPaneCreator)element.createExecutableExtension("class");
          }
          catch (CoreException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      }
    }

    throw new IllegalStateException("No facility pane creator for type " + type);
  }

  /**
   * @author Eike Stepper
   */
  private final class ActivateFacilityAction extends SafeAction
  {
    private final String type;

    private ActivateFacilityAction(String type, ImageDescriptor descriptor)
    {
      super(StringUtil.cap(type), AS_RADIO_BUTTON);
      setToolTipText("Activate " + type + " facility");
      setImageDescriptor(descriptor);
      this.type = type;
    }

    @Override
    protected void safeRun() throws Exception
    {
      if (activeCollaboration != null)
      {
        IFacility facility = activeCollaboration.getFacility(type);
        setActiveFacility(activeCollaboration, facility);
      }
    }
  }
}
