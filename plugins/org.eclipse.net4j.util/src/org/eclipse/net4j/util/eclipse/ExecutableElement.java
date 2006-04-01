/*******************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Sympedia Methods and Tools.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *******************************************************************************/
package org.eclipse.net4j.util.eclipse;


import org.eclipse.core.runtime.CoreException;


public class ExecutableElement extends Element
{
  protected String className;

  public String getClassName()
  {
    return className;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public Object createExecutableExtension() throws CoreException
  {
    String propertyName = executableExtensionPropertyName();
    return configurationElement.createExecutableExtension(propertyName);
  }

  public Object createExecutableExtension(String propertyName) throws CoreException
  {
    return configurationElement.createExecutableExtension(propertyName);
  }

  public String toString()
  {
    return "Executable(" + className + ")";
  }
}