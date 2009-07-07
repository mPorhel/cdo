/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/213402
 */
package org.eclipse.emf.cdo.internal.common.io;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOID.Type;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.id.CDOIDAndVersionImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalTempImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDMetaRangeImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectImpl;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOClearFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOContainerFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOUnsetFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOID;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public abstract class CDODataInputImpl extends ExtendedDataInput.Delegating implements CDODataInput
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDODataInputImpl.class);

  public CDODataInputImpl(ExtendedDataInput delegate)
  {
    super(delegate);
  }

  public CDOPackageUnit readCDOPackageUnit(CDOPackageRegistry packageRegistry) throws IOException
  {
    InternalCDOPackageUnit packageUnit = (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
    packageUnit.read(this, (InternalCDOPackageRegistry)packageRegistry);
    return packageUnit;
  }

  public CDOPackageUnit[] readCDOPackageUnits(CDOPackageRegistry packageRegistry) throws IOException
  {
    int size = readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} package units", size); //$NON-NLS-1$
    }

    CDOPackageUnit[] packageUnits = new CDOPackageUnit[size];
    for (int i = 0; i < size; i++)
    {
      packageUnits[i] = readCDOPackageUnit(packageRegistry);
    }

    return packageUnits;
  }

  public CDOPackageUnit.Type readCDOPackageUnitType() throws IOException
  {
    return CDOPackageUnit.Type.values()[readByte()];
  }

  public CDOPackageInfo readCDOPackageInfo() throws IOException
  {
    InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
    packageInfo.read(this);
    return packageInfo;
  }

  public String readCDOPackageURI() throws IOException
  {
    return getPackageURICompressor().read(this);
  }

  public CDOClassifierRef readCDOClassifierRef() throws IOException
  {
    return new CDOClassifierRef(this);
  }

  public EClassifier readCDOClassifierRefAndResolve() throws IOException
  {
    CDOClassifierRef classRef = readCDOClassifierRef();
    return classRef.resolve(getPackageRegistry());
  }

  public CDOType readCDOType() throws IOException
  {
    // TODO Use byte IDs
    int typeID = readInt();
    return CDOModelUtil.getType(typeID);
  }

  public CDOID readCDOID() throws IOException
  {
    byte ordinal = readByte();
    if (TRACER.isEnabled())
    {
      String type;
      try
      {
        type = Type.values()[ordinal].toString();
      }
      catch (RuntimeException ex)
      {
        type = ex.getMessage();
      }

      TRACER.format("Reading CDOID of type {0} ({1})", ordinal, type); //$NON-NLS-1$
    }

    Type type = Type.values()[ordinal];
    switch (type)
    {
    case NULL:
      return CDOID.NULL;

    case TEMP_OBJECT:
      return new CDOIDTempObjectImpl(readInt());

    case TEMP_META:
      return new CDOIDTempMetaImpl(readInt());

    case META:
      return new CDOIDMetaImpl(readLong());

    case EXTERNAL_OBJECT:
      return new CDOIDExternalImpl(readString());

    case EXTERNAL_TEMP_OBJECT:
      return new CDOIDExternalTempImpl(readString());

    case OBJECT:
    {
      CDOIDObject id = getIDFactory().createCDOIDObject(this);
      ((AbstractCDOID)id).read(this);
      return id;
    }

    default:
      throw new ImplementationError();
    }
  }

  public CDOIDAndVersion readCDOIDAndVersion() throws IOException
  {
    return new CDOIDAndVersionImpl(this);
  }

  public CDOIDMetaRange readCDOIDMetaRange() throws IOException
  {
    boolean exist = readBoolean();
    if (exist)
    {
      return new CDOIDMetaRangeImpl(readCDOID(), readInt());
    }

    return null;
  }

  public CDORevision readCDORevision() throws IOException
  {
    boolean notNull = readBoolean();
    if (notNull)
    {
      return new CDORevisionImpl(this);
    }

    return null;
  }

  public CDOList readCDOList(EClass owner, EStructuralFeature feature) throws IOException
  {
    int referenceChunk;
    int size = readInt();
    if (size < 0)
    {
      size = -size;
      referenceChunk = readInt();
      if (TRACER.isEnabled())
      {
        TRACER.format("Read feature {0}: size={1}, referenceChunk={2}", feature.getName(), size, referenceChunk); //$NON-NLS-1$
      }
    }
    else
    {
      referenceChunk = size;
      if (TRACER.isEnabled())
      {
        TRACER.format("Read feature {0}: size={1}", feature.getName(), size); //$NON-NLS-1$
      }
    }

    Object value = null;
    CDOType type = null;
    boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
    if (!isFeatureMap)
    {
      type = CDOModelUtil.getType(feature.getEType());
    }

    InternalCDOList list = (InternalCDOList)getListFactory().createList(size, size, referenceChunk);
    for (int j = 0; j < referenceChunk; j++)
    {
      if (isFeatureMap)
      {
        int featureID = readInt();
        EStructuralFeature innerFeature = owner.getEStructuralFeature(featureID);
        type = CDOModelUtil.getType(innerFeature.getEType());
        value = type.readValue(this);
        value = CDORevisionUtil.createFeatureMapEntry(innerFeature, value);
      }
      else
      {
        value = type.readValue(this);
      }

      list.set(j, value);
      if (TRACER.isEnabled())
      {
        TRACER.trace("    " + value); //$NON-NLS-1$
      }
    }

    return list;
  }

  public Object readCDOFeatureValue(EStructuralFeature feature) throws IOException
  {
    CDOType type = CDOModelUtil.getType(feature);
    if (type.canBeNull() && !feature.isMany())
    {
      if (readBoolean())
      {
        return InternalCDORevision.NIL;
      }
    }

    return type.readValue(this);
  }

  public CDORevisionDelta readCDORevisionDelta() throws IOException
  {
    return new CDORevisionDeltaImpl(this);
  }

  public CDOFeatureDelta readCDOFeatureDelta(EClass owner) throws IOException
  {
    int typeOrdinal = readInt();
    CDOFeatureDelta.Type type = CDOFeatureDelta.Type.values()[typeOrdinal];
    switch (type)
    {
    case ADD:
      return new CDOAddFeatureDeltaImpl(this, owner);

    case SET:
      return new CDOSetFeatureDeltaImpl(this, owner);

    case LIST:
      return new CDOListFeatureDeltaImpl(this, owner);

    case MOVE:
      return new CDOMoveFeatureDeltaImpl(this, owner);

    case CLEAR:
      return new CDOClearFeatureDeltaImpl(this, owner);

    case REMOVE:
      return new CDORemoveFeatureDeltaImpl(this, owner);

    case CONTAINER:
      return new CDOContainerFeatureDeltaImpl(this, owner);

    case UNSET:
      return new CDOUnsetFeatureDeltaImpl(this, owner);

    default:
      throw new IOException(MessageFormat.format(Messages.getString("CDODataInputImpl.5"), typeOrdinal)); //$NON-NLS-1$
    }
  }

  public Object readCDORevisionOrPrimitive() throws IOException
  {
    CDOType type = readCDOType();
    return type.readValue(this);
  }

  public Object readCDORevisionOrPrimitiveOrClassifier() throws IOException
  {
    boolean isClassifier = readBoolean();
    if (isClassifier)
    {
      return readCDOClassifierRefAndResolve();
    }

    return readCDORevisionOrPrimitive();
  }

  public LockType readCDOLockType() throws IOException
  {
    return readBoolean() ? LockType.WRITE : LockType.READ;
  }

  protected StringIO getPackageURICompressor()
  {
    return StringIO.DIRECT;
  }

  protected abstract CDOPackageRegistry getPackageRegistry();

  protected abstract CDORevisionResolver getRevisionResolver();

  protected abstract CDOIDObjectFactory getIDFactory();

  protected abstract CDOListFactory getListFactory();
}
