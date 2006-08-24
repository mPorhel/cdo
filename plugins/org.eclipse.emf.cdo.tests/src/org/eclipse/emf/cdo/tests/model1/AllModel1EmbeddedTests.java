/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.model1;


import org.eclipse.emf.cdo.tests.topology.ITopologyConstants;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AllModel1EmbeddedTests implements ITopologyConstants
{
  public static Test suite()
  {
    System.setProperty(CDO_TEST_MODE_KEY, EMBEDDED_MODE);
    TestSuite suite = new TestSuite("Embedded Test for org.eclipse.emf.cdo.tests.model1");
    suite.addTestSuite(BasicTest.class);
    suite.addTestSuite(AdapterTest.class);
    suite.addTestSuite(SerializationTest.class);
    suite.addTestSuite(NotificationTest.class);
    suite.addTestSuite(ExtentTest.class);
    suite.addTestSuite(OCLTest.class);
    suite.addTestSuite(Bugzilla154389Test.class);
    return suite;
  }
}
