/**
 * Copyright (c) 2018 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    lfasani - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.view.CDOViewImpl;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl.OptionsImpl;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import java.io.IOException;
import java.util.Collections;

/**
 * This class aim to test the behavior of the LockStatePrefetcher.
 * @author lfasani
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class LockStatePrefetcherTest extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/test1";

  private CDOSession cdoSession;

  private CDOTransaction cdoTransaction;

  private CDOResource cdoResource;

  private Company company;

  private Category category1;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    cdoSession = openSession();
    cdoSession.options().setLockNotificationMode(LockNotificationMode.ALWAYS);
    cdoTransaction = cdoSession.openTransaction();
    ((OptionsImpl)cdoTransaction.options()).setLockStatePrefetchEnabled(true);
    cdoResource = cdoTransaction.createResource(getResourcePath(RESOURCE_PATH));

    company = getModel1Factory().createCompany();
    category1 = getModel1Factory().createCategory();
    category1.setName("name");
    company.getCategories().add(category1);

    cdoResource.getContents().add(company);
    cdoResource.save(Collections.emptyMap());
  }

  /**
   * Test that, when loading a CDOObject in a CDOView with previous time stamp, no ObjectNotFoundException will thrown when this object does not exist anymore in a view with other time stamp.
   */
  public void testLockStatePrefetcherWithNomoreExistingObject() throws Exception
  {
    final boolean[] errorOccurred = { false };
    ILogListener logListener = new ILogListener()
    {
      public void logging(IStatus status, String plugin)
      {
        if (status.getException() instanceof ObjectNotFoundException)
        {
          errorOccurred[0] = true;
        }
      }
    };
    Platform.addLogListener(logListener);

    try
    {
      CDOID cdoIDCategory1 = CDOUtil.getCDOObject(category1).cdoID();
      company.getCategories().clear();
      long lastCommit = cdoSession.getCommitInfoManager().getLastCommit();
      cdoResource.save(Collections.emptyMap());

      // open a view in which category1 exists
      CDOView cdoView = cdoSession.openView(lastCommit);
      ((OptionsImpl)cdoView.options()).setLockStatePrefetchEnabled(true);
      CDOObject object = cdoView.getObject(cdoIDCategory1);

      assertNotNull("The deleted category1 object in branch master should be found in this view corresponding to old time stamp", object);
      assertFalse("An ObjectNotFoundException has been thrown", errorOccurred[0]);
    }
    finally
    {
      Platform.removeLogListener(logListener);
    }
  }

  /**
   * Test that when loading an CDOObject(with its revision) in a CDOView with the current time stamp, it will not load the revision of other CDOView with an older time stamp.
   */
  public void testLockStatePrefetcherAvoidLoadingRevisionInOlderView() throws Exception
  {
    testLockStatePrefetcherAvoidLoadingRevisionInOtherView(true);
  }

  /**
   * Test that when loading an CDOObject(with its revision) in a CDOView with an old time stamp, it will not load the revision of other CDOView with the current time stamp.
   */
  public void testLockStatePrefetcherAvoidLoadingRevisionInCurrentView() throws Exception
  {
    testLockStatePrefetcherAvoidLoadingRevisionInOtherView(false);
  }

  private void testLockStatePrefetcherAvoidLoadingRevisionInOtherView(boolean otherViewIsOlder) throws IOException
  {
    IModelConfig modelConfig = getModelConfig();
    // the test fail in Legacy mode
    if (IConstants.NATIVE.equals(modelConfig))
    {
      CDOID cdoIDCategory1 = CDOUtil.getCDOObject(category1).cdoID();
      category1.setName("New name");
      long lastCommit = cdoSession.getCommitInfoManager().getLastCommit();
      cdoResource.save(Collections.emptyMap());
      cdoTransaction.close();
      cdoSession.close();

      // open a two views where category1 has a different revision
      cdoSession = openSession();
      CDOView cdoView1 = cdoSession.openView(otherViewIsOlder ? 0 : lastCommit);
      ((OptionsImpl)cdoView1.options()).setLockStatePrefetchEnabled(true);
      CDOView cdoView2 = cdoSession.openView(otherViewIsOlder ? lastCommit : 0);
      ((OptionsImpl)cdoView2.options()).setLockStatePrefetchEnabled(true);

      cdoView1.getObject(cdoIDCategory1);
      InternalCDORevision revision = ((CDOViewImpl)cdoView1).getRevision(cdoIDCategory1, false);
      assertNotNull("The revision of category1 object should be loaded in the view", revision);

      // check that revision of cdoViewPrevious are not loaded
      revision = ((CDOViewImpl)cdoView2).getRevision(cdoIDCategory1, false);

      assertNull("The revision of category1 object should not be loaded in the view with other time stamp", revision);
    }
  }

}
