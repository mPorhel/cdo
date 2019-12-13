/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db;

import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.util.security.IUserAware;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @since 4.2
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IDBConnection extends Connection, IUserAware
{
  public IDBDatabase getDatabase();

  public IDBSchemaTransaction openSchemaTransaction();

  public IDBPreparedStatement prepareStatement(String sql, ReuseProbability reuseProbability);

  public IDBPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, ReuseProbability reuseProbability);

  /**
   * @deprecated Not supported.
   */
  @Override
  @Deprecated
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException;

  /**
   * @deprecated Not supported.
   */
  @Override
  @Deprecated
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException;

  /**
   * @deprecated Not supported.
   */
  @Override
  @Deprecated
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException;

  /**
   * @deprecated Not supported.
   */
  @Override
  @Deprecated
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException;
}
