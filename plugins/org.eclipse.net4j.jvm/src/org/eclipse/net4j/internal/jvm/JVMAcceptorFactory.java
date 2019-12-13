/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.spi.net4j.AcceptorFactory;

/**
 * @author Eike Stepper
 */
public class JVMAcceptorFactory extends AcceptorFactory
{
  public static final String TYPE = "jvm"; //$NON-NLS-1$

  public JVMAcceptorFactory()
  {
    super(TYPE);
  }

  @Override
  public JVMAcceptor create(String description) throws ProductCreationException
  {
    JVMAcceptor acceptor = new JVMAcceptor();
    acceptor.setName(description);
    return acceptor;
  }

  @Override
  public String getDescriptionFor(Object acceptor)
  {
    if (acceptor instanceof JVMAcceptor)
    {
      return ((JVMAcceptor)acceptor).getName();
    }

    return null;
  }

  public static JVMAcceptor get(IManagedContainer container, String description)
  {
    return (JVMAcceptor)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
