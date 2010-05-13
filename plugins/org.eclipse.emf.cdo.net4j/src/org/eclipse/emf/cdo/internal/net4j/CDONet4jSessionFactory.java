/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.security.CredentialsProviderFactory;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 */
public class CDONet4jSessionFactory extends CDOSessionFactory
{
  public static final String TYPE = "cdo"; //$NON-NLS-1$

  public CDONet4jSessionFactory()
  {
    super(TYPE);
  }

  /**
   * @since 2.0
   */
  @Override
  protected InternalCDOSession createSession(String repositoryName, boolean automaticPackageRegistry)
  {
    CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
    configuration.setRepositoryName(repositoryName);
    configuration.getAuthenticator().setCredentialsProvider(getCredentialsProvider());

    // The session will be activated by the container
    configuration.setActivateOnOpen(false);
    return (InternalCDOSession)configuration.openSession();
  }

  protected IPasswordCredentialsProvider getCredentialsProvider()
  {
    try
    {
      IManagedContainer container = getManagedContainer();
      String type = getCredentialsProviderType();
      return (IPasswordCredentialsProvider)container.getElement(CredentialsProviderFactory.PRODUCT_GROUP, type, null);
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  protected IManagedContainer getManagedContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected String getCredentialsProviderType()
  {
    return "interactive";
  }

  public static CDOSession get(IManagedContainer container, String description)
  {
    return (CDOSession)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
