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
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.ConnectorException;
import org.eclipse.net4j.ConnectorLocation;
import org.eclipse.net4j.ConnectorState;

import java.nio.channels.SelectionKey;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class TCPServerConnector extends TCPConnector
{
  private TCPAcceptor acceptor;

  public TCPServerConnector(TCPAcceptor acceptor)
  {
    this.acceptor = acceptor;
  }

  public TCPAcceptor getAcceptor()
  {
    return acceptor;
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.SERVER;
  }

  @Override
  public void setState(ConnectorState newState) throws ConnectorException
  {
    super.setState(newState);
  }

  @Override
  public String getHost()
  {
    try
    {
      return getSocketChannel().socket().getInetAddress().getHostAddress();
    }
    catch (RuntimeException ex)
    {
      return null;
    }
  }

  @Override
  public int getPort()
  {
    try
    {
      return getSocketChannel().socket().getPort();
    }
    catch (RuntimeException ex)
    {
      return 0;
    }
  }

  @Override
  public String toString()
  {
    if (getUserID() == null)
    {
      return MessageFormat.format("ServerTCPConnector[{0}:{1}]", getHost(), getPort()); //$NON-NLS-1$
    }
    else
    {
      return MessageFormat.format("ServerTCPConnector[{3}@{0}:{1}]", getHost(), getPort(), getUserID()); //$NON-NLS-1$
    }
  }

  @Override
  public void handleRegistration(SelectionKey selectionKey)
  {
    super.handleRegistration(selectionKey);
    acceptor.addConnector(this);
  }
}
