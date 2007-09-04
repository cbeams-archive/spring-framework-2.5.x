/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.jdbc.core.simple.metadata;

import org.springframework.jdbc.core.SqlParameter;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface specifying the API to be implemented by a class providing call metedata.  This is intended for internal use
 * by the Simple JDBC classes.
 *
 * @author Thomas Risberg
 * @since 2.1
 */
public interface CallMetaDataProvider {

	/**
	 * Initialize using the provided DatabaseMetData.
	 * @param databaseMetaData used to retreive database specific information
	 * @throws SQLException
	 */
	void initializeWithMetaData(DatabaseMetaData databaseMetaData) throws SQLException;

	/**
	 * Initialize the database specific management of procedure column meta data.  This is only called for
	 * databases that are supported.  This initalization can be turned off by specifying that column meta data
	 * should not be used.
	 *
	 * @param databaseMetaData used to retreive database specific information
	 * @param catalogName name of catalog to use or null
	 * @param schemaName name of schema name to use or null
	 * @param procedureName name of the stored procedure
	 * @throws SQLException
	 * @see	org.springframework.jdbc.core.simple.SimpleJdbcCall#withoutProcedureColumnMetaDataAccess()
	 */
	void initializeWithProcedureColumnMetaData(DatabaseMetaData databaseMetaData, String catalogName, String schemaName, String procedureName) throws SQLException;

	/**
	 * Provide any modification of the procedure name passed in to match the meta data currently used.
	 * This could include alterig the case.
	 * @param procedureName
	 */
	String procedureNameToUse(String procedureName);

	/**
	 * Provide any modification of the catalog name passed in to match the meta data currently used.
	 * This could include alterig the case.
	 * @param catalogName
	 */
	String catalogNameToUse(String catalogName);

	/**
	 * Provide any modification of the schema name passed in to match the meta data currently used.
	 * This could include alterig the case.
	 * @param schemaName
	 */
	String schemaNameToUse(String schemaName);

	/**
	 * Provide any modification of the catalog name passed in to match the meta data currently used.
	 * The reyurned value will be used for meta data lookups.  This could include alterig the case used or
	 * providing a base catalog if mone provided.
	 * @param catalogName
	 */
	String metaDataCatalogNameToUse(String catalogName) ;

	/**
	 * Provide any modification of the schema name passed in to match the meta data currently used.
	 * The reyurned value will be used for meta data lookups.  This could include alterig the case used or
	 * providing a base schema if mone provided.
	 * @param schemaName
	 */
	String metaDataSchemaNameToUse(String schemaName) ;

	/**
	 * Provide any modification of the column name passed in to match the meta data currently used.
	 * This could include alterig the case.
	 * @param parameterName name of the parameter of column
	 */
	String parameterNameToUse(String parameterName);

	/**
	 * Create a default out parameter based on the provided meta data.  This is used when no expicit
	 * parameter declaration has been made.
	 * @param parameterName the name of the parameter
	 * @param meta meta data used for this call
	 * @return the configured SqlParameter
	 */
	SqlParameter createDefaultOutParameter(String parameterName, CallParameterMetaData meta);

	/**
	 * Create a default in parameter based on the provided meta data.  This is used when no expicit
	 * parameter declaration has been made.
	 * @param parameterName the name of the parameter
	 * @param meta meta data used for this call
	 * @return the configured SqlParameter
	 */
	SqlParameter createDefaultInParameter(String parameterName, CallParameterMetaData meta);

	/**
	 * Get the name of the current user.  Useful for meta data lookups etc.
	 * @return current user name from database connection
	 */
	String getUserName();

	/**
	 * Does this database support returning resultsets that should be retreived with the JDBC call
	 * {@link java.sql.Statement#getResultSet()}
	 */
	boolean isReturnResultSetSupported();

	/**
	 * Does this database support returning resultsets as ref cursors to be retreived with
	 * {@link java.sql.CallableStatement#getObject(int)} for the specified column.
	 */
	boolean isRefCursorSupported();

	/**
	 * Get the {@link java.sql.Types} type for columns that return resultsets as ref cursors if this feature
	 * is supported.
	 */
	int getRefCursorSqlType();

	/**
	 * Are we using the meta data for the procedure columns?
	 * @return
	 */
	boolean isProcedureColumnMetaDataUsed();

	/**
	 * Should we bypass the return parameter with the specified name. This allows the database specific implementation
	 * to skip the processing for specific results returned by the database call.
	 * @param parameterName
	 */
	boolean byPassReturnParameter(String parameterName);

	/**
	 * Get the call parameter metadata that is currently used.
	 * @return List of {@link CallParameterMetaData}
	 */
	List<CallParameterMetaData> getCallParameterMetaData();

}