package org.example.kickoff.arquillian;

import static org.dbunit.operation.DatabaseOperation.CLEAN_INSERT;
import static org.dbunit.operation.DatabaseOperation.NONE;

import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.JndiDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;

public abstract class ArquillianDBUnitTestBase {

	protected abstract String getLookupName();

	protected abstract IDataSet getTestDataSet() throws Exception;

	protected DatabaseOperation getSetupOperation() {
		return CLEAN_INSERT;
	}

	protected DatabaseOperation getTeardownOperation() {
		return NONE;
	}

	protected void setupDatabaseConfig(DatabaseConfig config) throws Exception {
	}

	@Before
	public void beforeTest() throws DatabaseUnitException, SQLException, Exception {
		performDatabaseOperation(getSetupOperation());
	}

	@After
	public void afterTest() throws DatabaseUnitException, SQLException, Exception {
		performDatabaseOperation(getTeardownOperation());
	}

	private void performDatabaseOperation(DatabaseOperation databaseOperation) throws DatabaseUnitException, SQLException, Exception {
		IDataSet testDataSet = getTestDataSet();

		if (NONE.equals(getSetupOperation()) || testDataSet == null) {
			return;
		}

		IDatabaseConnection connection = getConnection();

		try {
			setupDatabaseConfig(connection.getConfig());

			databaseOperation.execute(connection, testDataSet);
		}
		finally {
			connection.close();
		}
	}

	private IDatabaseConnection getConnection() throws Exception {
		return new JndiDatabaseTester(getLookupName()).getConnection();
	}
}
