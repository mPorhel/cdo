/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.topology;


import org.eclipse.emf.cdo.client.CDOResource;
import org.eclipse.emf.cdo.client.ResourceManager;
import org.eclipse.emf.cdo.client.impl.CDOResourceFactoryImpl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import junit.framework.TestCase;


public abstract class AbstractTopologyTest extends TestCase implements ITopologyConstants
{
  private static int run;

  private String mode;

  private ITopology topology;

  private long startMemory;

  private long startTime;

  private long runTime;

  private String label;

  public String getMode()
  {
    return mode;
  }

  public void setMode(String mode)
  {
    this.mode = mode;
  }

  public ITopology getTopology()
  {
    return topology;
  }

  public void setTopology(ITopology topology)
  {
    this.topology = topology;
  }

  @Override
  protected void setUp() throws Exception
  {
    startTime = System.currentTimeMillis();
    //    remoteTraceServer = new RemoteTraceServer();
    //    remoteTraceServer.addListener(RemoteTraceServer.PrintListener.CONSOLE);

    OMPlatform.INSTANCE.setDebugging(true);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    //    OMPlatform.INSTANCE.addTraceHandler(new RemoteTraceHandler());
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);

    System.gc();
    startMemory = getUsedMemory();

    if (topology == null) topology = createTopology();
    label = getName() + " [" + topology.getName() + "]";
    System.out.println("=========================================================================");
    System.out.println("TC_START " + label);
    System.out.println("=========================================================================");

    super.setUp();
    topology.start();

    startTime = System.currentTimeMillis() - startTime;
    runTime = System.currentTimeMillis();
  }

  @Override
  protected void tearDown() throws Exception
  {
    topology.waitForSignals();
    runTime = System.currentTimeMillis() - runTime;

    Thread.sleep(100);
    long stopTime = System.currentTimeMillis();

    JdbcTemplate jdbc = jdbc();
    wipeDatabase(jdbc);

    topology.stop();
    topology = null;
    super.tearDown();

    System.gc();
    long endMemory = getUsedMemory();
    stopTime = System.currentTimeMillis() - stopTime;

    String run = getRun();
    System.out.println("Runtime-Stat " + run + "\t " + startTime + "\t " + runTime + "\t "
        + stopTime);
    System.out.println("Memory-Delta " + run + "\t " + (endMemory - startMemory));
    System.out.println("=========================================================================");
    System.out.println("TC_END " + label);
    System.out.println("=========================================================================");
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    label = null;
    mode = null;
  }

  private static long getUsedMemory()
  {
    return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
  }

  private static String getRun()
  {
    return String.format("%4d", new Object[] { ++run});
  }

  protected void wipeDatabase(JdbcTemplate jdbc)
  {
    dropTable(jdbc, "CDO_ATTRIBUTE");
    dropTable(jdbc, "CDO_CLASS");
    dropTable(jdbc, "CDO_CONTENT");
    dropTable(jdbc, "CDO_OBJECT");
    dropTable(jdbc, "CDO_PACKAGE");
    dropTable(jdbc, "CDO_REFERENCE");
    dropTable(jdbc, "CDO_RESOURCE");
  }

  protected void dropTable(JdbcTemplate jdbc, String tableName)
  {
    try
    {
      jdbc.execute("DROP TABLE " + tableName);
    }
    catch (Exception ignore)
    {
      ; // Intentionally left empty
    }
  }

  protected DataSource getDataSource()
  {
    return topology.getDataSource();
  }

  protected JdbcTemplate jdbc()
  {
    return new JdbcTemplate(getDataSource());
  }

  protected ResourceManager createResourceManager(ResourceSet resourceSet) throws Exception
  {
    return topology.createResourceManager(resourceSet);
  }

  protected ResourceManager createResourceManager() throws Exception
  {
    return createResourceManager(new ResourceSetImpl());
  }

  protected CDOResource createResource(String path, ResourceManager resourceManager)
  {
    URI uri = CDOResourceFactoryImpl.formatURI(path);
    return (CDOResource) resourceManager.createResource(uri);
  }

  protected CDOResource createResource(String path) throws Exception
  {
    return createResource(path, createResourceManager());
  }

  protected CDOResource getResource(String path, ResourceManager resourceManager)
  {
    URI uri = CDOResourceFactoryImpl.formatURI(path);
    return (CDOResource) resourceManager.getResource(uri, true);
  }

  protected CDOResource getResource(String path) throws Exception
  {
    return getResource(path, createResourceManager());
  }

  protected EObject loadRoot(String path) throws Exception
  {
    CDOResource resource = getResource(path);
    return resource.getContents().get(0);
  }

  protected CDOResource saveRoot(EObject root, String path) throws Exception
  {
    CDOResource resource = createResource(path);
    resource.getContents().add(root);
    resource.save(null);
    return resource;
  }

  protected ITopology createTopology()
  {
    return new ClientServerTopology();
    //    if (mode == null)
    //    {
    //      mode = System.getProperty(CDO_TEST_MODE_KEY, DEFAULT_MODE);
    //    }
    //
    //    if (EMBEDDED_MODE.equalsIgnoreCase(mode))
    //    {
    //      return new EmbeddedTopology();
    //    }
    //
    //    if (CLIENT_MODE.equalsIgnoreCase(mode))
    //    {
    //      return new ClientTopology();
    //    }
    //
    //    if (CLIENT_SERVER_MODE.equalsIgnoreCase(mode))
    //    {
    //      return new ClientServerTopology();
    //    }
    //
    //    if (CLIENT_SEPARATED_SERVER_MODE.equalsIgnoreCase(mode))
    //    {
    //      return new ClientSeparatedServerTopology();
    //    }
    //
    //    fail("Topology not recognized: " + mode);
    //    return null; // Make compiler happy
  }

  protected void assertTrue(Object object)
  {
    assertNotNull(object);
    assertSame(Boolean.class, object.getClass());
    assertTrue(((Boolean) object).booleanValue());
  }

  protected void assertFalse(Object object)
  {
    assertNotNull(object);
    assertSame(Boolean.class, object.getClass());
    assertFalse(((Boolean) object).booleanValue());
  }
}
