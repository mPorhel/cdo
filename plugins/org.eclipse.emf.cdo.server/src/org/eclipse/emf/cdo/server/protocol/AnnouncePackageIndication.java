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
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.core.impl.AbstractIndicationWithResponse;

import org.eclipse.emf.cdo.core.CdoProtocol;
import org.eclipse.emf.cdo.server.CdoServerProtocol;
import org.eclipse.emf.cdo.server.ClassInfo;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.PackageInfo;


public class AnnouncePackageIndication extends AbstractIndicationWithResponse
{
  private String packageName;

  public short getSignalId()
  {
    return CdoProtocol.ANNOUNCE_PACKAGE;
  }

  public void indicate()
  {
    packageName = receiveString();
    if (isDebugEnabled()) debug("Announced package " + packageName);
  }

  public void respond()
  {
    Mapper mapper = ((CdoServerProtocol) getProtocol()).getMapper();
    PackageInfo packageInfo = mapper.getPackageManager().getPackageInfo(packageName);

    if (packageInfo == null)
    {
      if (isDebugEnabled()) debug("Unknown package " + packageName);
      transmitInt(-1);
    }
    else
    {
      ClassInfo[] classInfos = packageInfo.getClasses();
      transmitInt(classInfos.length);

      for (int i = 0; i < classInfos.length; i++)
      {
        ClassInfo classInfo = classInfos[i];
        if (isDebugEnabled())
          debug("Responding class " + classInfo.getName() + " = " + classInfo.getCid());

        transmitInt(classInfo.getCid());
        transmitString(classInfo.getName());
      }
    }
  }
}
