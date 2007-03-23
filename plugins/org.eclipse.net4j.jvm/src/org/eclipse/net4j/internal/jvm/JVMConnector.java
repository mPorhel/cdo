/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.transport.ConnectorException;
import org.eclipse.net4j.transport.IBuffer;
import org.eclipse.net4j.transport.IChannel;
import org.eclipse.net4j.transport.IProtocol;

import org.eclipse.internal.net4j.transport.Channel;
import org.eclipse.internal.net4j.transport.Connector;

import java.util.Queue;

/**
 * TODO Remove peer channels
 * 
 * @author Eike Stepper
 */
public abstract class JVMConnector extends Connector
{
  private JVMConnector peer;

  private String name;

  public JVMConnector()
  {
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public JVMConnector getPeer()
  {
    return peer;
  }

  public void setPeer(JVMConnector peer)
  {
    this.peer = peer;
  }

  @Override
  protected void registerChannelWithPeer(short channelIndex, IProtocol protocol) throws ConnectorException
  {
    try
    {
      Channel channel = getPeer().createChannel(channelIndex, protocol);
      if (channel == null)
      {
        throw new ConnectorException("Failed to register channel with peer"); //$NON-NLS-1$
      }

      channel.activate();
    }
    catch (ConnectorException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ConnectorException(ex);
    }
  }

  public void multiplexBuffer(IChannel localChannel)
  {
    short channelIndex = localChannel.getChannelIndex();
    Channel peerChannel = peer.getChannel(channelIndex);
    if (peerChannel == null)
    {
      throw new IllegalStateException("peerChannel == null"); //$NON-NLS-1$
    }

    Queue<IBuffer> localQueue = ((Channel)localChannel).getSendQueue();
    IBuffer buffer = localQueue.poll();
    buffer.flip();
    peerChannel.handleBufferFromMultiplexer(buffer);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (name == null)
    {
      throw new IllegalStateException("name == null");
    }
  }
}
