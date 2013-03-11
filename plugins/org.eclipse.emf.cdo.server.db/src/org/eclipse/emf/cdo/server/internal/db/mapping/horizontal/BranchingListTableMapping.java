/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - derived branch mapping from audit mapping
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBTable;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * This is a list-table mapping for audit mode. It has ID and version columns and no delta support.
 *
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 3.0
 */
public class BranchingListTableMapping extends AbstractListTableMapping
{
  private String sqlClear;

  public BranchingListTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
    initSQLStrings();
  }

  private void initSQLStrings()
  {
    IDBTable table = getTable();

    // ----------- clear list -------------------------
    StringBuilder builder = new StringBuilder();
    builder.append("DELETE FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(LIST_REVISION_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(LIST_REVISION_BRANCH);
    builder.append("=?  AND "); //$NON-NLS-1$
    builder.append(LIST_REVISION_VERSION);
    builder.append("=?"); //$NON-NLS-1$
    sqlClear = builder.toString();
  }

  @Override
  protected void addKeyFields(List<FieldInfo> list)
  {
    list.add(new FieldInfo(LIST_REVISION_BRANCH, DBType.INTEGER));
    list.add(new FieldInfo(LIST_REVISION_VERSION, DBType.INTEGER));
  }

  @Override
  protected void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    idHandler.setCDOID(stmt, 1, revision.getID());
    stmt.setInt(2, revision.getBranch().getID());
    stmt.setInt(3, revision.getVersion());
  }

  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    // the audit list mapping does not care about revised references -> NOP
  }

  @Override
  public void rawDeleted(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlClear, ReuseProbability.HIGH);
      idHandler.setCDOID(stmt, 1, id);
      stmt.setInt(2, branch.getID());
      stmt.setInt(3, version);
      DBUtil.update(stmt, false);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }
}
