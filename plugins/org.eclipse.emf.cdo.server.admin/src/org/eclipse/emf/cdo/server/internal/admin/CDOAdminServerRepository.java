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
package org.eclipse.emf.cdo.server.internal.admin;

import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOAdminServerRepository extends Notifier implements CDOAdminRepository
{
  private final CDOAdminServer admin;

  private final IRepository delegate;

  private IListener delegateListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof TypeChangedEvent)
      {
        TypeChangedEvent e = (TypeChangedEvent)event;
        Type oldType = e.getOldType();
        Type newType = e.getNewType();

        fireEvent(new RepositoryTypeChangedEvent(CDOAdminServerRepository.this, oldType, newType));
        admin.repositoryTypeChanged(getName(), oldType, newType);
      }
      else if (event instanceof StateChangedEvent)
      {
        StateChangedEvent e = (StateChangedEvent)event;
        State oldState = e.getOldState();
        State newState = e.getNewState();

        fireEvent(new RepositoryStateChangedEvent(CDOAdminServerRepository.this, oldState, newState));
        admin.repositoryStateChanged(getName(), oldState, newState);
      }
    }
  };

  public CDOAdminServerRepository(CDOAdminServer admin, IRepository delegate)
  {
    this.admin = admin;
    this.delegate = delegate;
    delegate.addListener(delegateListener);
  }

  public final CDOAdmin getAdmin()
  {
    return admin;
  }

  public final IRepository getDelegate()
  {
    return delegate;
  }

  public boolean delete(String type)
  {
    return admin.deleteRepository(this, type);
  }

  public String getName()
  {
    return delegate.getName();
  }

  public String getUUID()
  {
    return delegate.getUUID();
  }

  public Type getType()
  {
    return delegate.getType();
  }

  public State getState()
  {
    return delegate.getState();
  }

  public String getStoreType()
  {
    return delegate.getStoreType();
  }

  public Set<ObjectType> getObjectIDTypes()
  {
    return delegate.getObjectIDTypes();
  }

  public long getCreationTime()
  {
    return delegate.getCreationTime();
  }

  public CDOID getRootResourceID()
  {
    return delegate.getRootResourceID();
  }

  public boolean isSupportingAudits()
  {
    return delegate.isSupportingAudits();
  }

  public boolean isSupportingBranches()
  {
    return delegate.isSupportingBranches();
  }

  public boolean isSupportingEcore()
  {
    return delegate.isSupportingEcore();
  }

  public boolean isEnsuringReferentialIntegrity()
  {
    return delegate.isEnsuringReferentialIntegrity();
  }

  public IDGenerationLocation getIDGenerationLocation()
  {
    return delegate.getIDGenerationLocation();
  }

  public long getTimeStamp() throws UnsupportedOperationException
  {
    return delegate.getTimeStamp();
  }

  @Override
  public String toString()
  {
    return delegate.toString();
  }

  public void write(ExtendedDataOutputStream out) throws IOException
  {
    out.writeString(getName());
    out.writeString(getUUID());
    out.writeEnum(getType());
    out.writeEnum(getState());
    out.writeLong(getCreationTime());
    out.writeString(getStoreType());

    Set<CDOID.ObjectType> objectIDTypes = getObjectIDTypes();
    int types = objectIDTypes.size();
    out.writeInt(types);
    for (CDOID.ObjectType objectIDType : objectIDTypes)
    {
      out.writeEnum(objectIDType);
    }

    out.writeBoolean(isSupportingAudits());
    out.writeBoolean(isSupportingBranches());
    out.writeBoolean(isSupportingEcore());
    out.writeBoolean(isEnsuringReferentialIntegrity());
    out.writeEnum(getIDGenerationLocation());
  }

  public void dispose()
  {
    delegate.removeListener(delegateListener);
  }
}
