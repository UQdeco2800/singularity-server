/**
 * 
 */
package uq.deco2800.singularity.integration.test.util;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.State;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.DBIException;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.dropwizard.db.DataSourceFactory;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.util.ServerUtils;
import uq.deco2800.singularity.server.ServerConfiguration;
import uq.deco2800.singularity.server.SingularityServer;

/**
 * A Test Environment used to help run integration tests on a database and the data access objects used to interface
 * with the database. Also used to create a SingularityServer Instance with randomised ports in order to create an
 * environment to test the clients and server interaction in a thread and port safe manner.
 * 
 * @author dion-loetscher
 * 
 */
public class TestEnvironment {

	/**
	 * Used to read and write YAML files to {@link ServerConfiguration} objects and {@link JsonNode} objects so that
	 * configuration files can be modified and saved.
	 */
	private static final YamlHelper<ServerConfiguration> YAML_HELPER = new YamlHelper<>(ServerConfiguration.class);

	/**
	 * The name of the temporary configuration file made by {@link #setupConfiguration()}
	 */
	private String tempConfigurationFileName;

	/**
	 * The temporary configuration made by {@link #setupConfiguration()}
	 */
	private ServerConfiguration configuration;

	/**
	 * The temporary configuration made by {@link #setupConfiguration()}
	 */
	private JsonNode jsonConfiguration;

	/**
	 * A handle into the Database created by {@link #setupDbConnection()}. Used by {@link #migrateDb()} and
	 * {@link #tearDownDb()}
	 */
	private Handle handle;

	/**
	 * The actual connection to the database as setup by {@link #setupDbConnection()}. Used to create {@link #handle}.
	 */
	private DBI dbi;

	/**
	 * The thread the server runs on as created by {@link #setupServer()}.
	 */
	private Thread serverThread;

	/**
	 * The actual singularity server which is running on {@link #serverThread}. Uses the configuration files set up by
	 * {@link #setupConfiguration()}.
	 */
	private SingularityServer activeServer;

	/**
	 * Used to format the dates when creating the temporary in memory Derby instance as well as the
	 * {@link #tempConfigurationFileName}
	 */
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddhhmmss");

	/**
	 * The name of this class. Currently used for the {@link #LOGGER}
	 */
	private static final String CLASS = TestEnvironment.class.getName();

	/**
	 * The SL4J {@link Logger} used for logging within this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	/**
	 * Sets up a temporary configuration file for use with this TestEnvironment. Randomises all ports set in the
	 * configuration file. Currently randomises the REST port, REST Admin port and the Messaging Port. Also specifies
	 * the database to be a random in memory database (Connects to database <em>testDB-{Current Time}-{Random UUID}</em>
	 * ) as opposed to an on disk database.
	 * 
	 * @throws JsonParseException
	 *             Thrown if there are errors parsing in the original configuration.
	 * @throws JsonMappingException
	 *             Thrown if there are errors parsing in the original configuration.
	 * @throws IOException
	 *             Thrown if there are problems reading the original configuration or writing the temporary
	 *             configuration.
	 * @throws URISyntaxException
	 */
	public void setupConfiguration() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		jsonConfiguration = (ObjectNode) YAML_HELPER
				.readFileToJson(getClass().getResource("/configuration.yaml").toURI());

		String dbName = "testDB-" + dateFormatter.format(new Date()) + "-" + UUID.randomUUID().toString();
		// Should guarantee a different DB for every new TestEnvironment.

		LOGGER.info("Using [{}] in-memory DB", dbName);
		// Can cast JsonNode to ObjectNode to get access to the put method.
		((ObjectNode) jsonConfiguration.get("database")).put("url", "jdbc:derby:memory:" + dbName + ";create=true");
		((ObjectNode) jsonConfiguration.get("messagingConfiguration")).put("port", ServerUtils.getAvailablePort());
		((ObjectNode) jsonConfiguration.get("messagingConfiguration")).put("port", ServerUtils.getAvailablePort());
		((ObjectNode) jsonConfiguration).remove("logging");
		((ObjectNode) jsonConfiguration).put("shouldStartLoggingService", false);

		String[] connectorTypes = { "applicationConnectors", "adminConnectors" };
		for (String serverType : connectorTypes) {
			Iterator<JsonNode> serverIterator = jsonConfiguration.get("server").get(serverType).iterator();
			while (serverIterator.hasNext()) {
				ObjectNode node = (ObjectNode) serverIterator.next();
				node.put("port", ServerUtils.getAvailablePort());
			}
		}

		// Create then store reference to file
		tempConfigurationFileName = YAML_HELPER.createTemporaryYamlFromJson("test", jsonConfiguration);
		configuration = YAML_HELPER.readFileToObject(tempConfigurationFileName);
		LOGGER.info("Stored temporary yaml file:  [{}]", tempConfigurationFileName);
	}

	/**
	 * Creates a Database Instance object to connect to the database and creates a handle to use to perform migration
	 * and database emptying.
	 */
	public void setupDbConnection() {
		DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();
		if (dbi == null || handle == null) {
			dbi = new DBI(dataSourceFactory.getUrl(), dataSourceFactory.getUser(), dataSourceFactory.getPassword());
			handle = dbi.open();
		}
	}

	/**
	 * Retrieves all the files in the migration resource and applies the SQL files in lexicographical order.
	 * 
	 * @throws URISyntaxException
	 */
	public void migrateDb() throws URISyntaxException {
		URI sqlDirectoryUrl = getClass().getResource("/db/migration").toURI();
		File sqlDirectory = new File(sqlDirectoryUrl);
		File[] files = sqlDirectory.listFiles();
		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				String file1 = o1.getName();
				String file2 = o2.getName();
				file1 = file1.split("_")[0];
				file2 = file2.split("_")[0];
				file1 = file1.substring(1);
				file2 = file2.substring(1);
				int version1 = Integer.parseInt(file1);
				int version2 = Integer.parseInt(file2);
				return Integer.compare(version1, version2);
			}
		});
		for (File sqlFile : files) {
			try (Scanner scanner = new Scanner(sqlFile)) {
				StringBuilder sqlQuery = new StringBuilder();
				while (scanner.hasNextLine()) {
					sqlQuery.append(scanner.nextLine());
				}
				handle.createScript(sqlQuery.toString()).execute();
			} catch (IOException e) {
				LOGGER.warn("Could not get file", e);
				continue;
			}
		}
	}

	/**
	 * Retrieves all tables and views in the database and drops them. If there are Triggers, this may cause an issue.
	 * <br>
	 * <br>
	 * <strong>TODO:</strong> remove all triggers before removing tables and views.
	 */
	public void emptyDb() {
		Connection conn = handle.getConnection();
		DatabaseMetaData dbMetadata;
		try {
			dbMetadata = conn.getMetaData();
			String[] tableTypesToDrop = { "TABLE", "VIEW" };
			// Other table types include SYSTEM_TABLE
			boolean retry = true;
			ResultSet results = dbMetadata.getTables(null, null, "%", tableTypesToDrop);
			List<String> tables = new LinkedList<String>();
			while (results.next()) {
				// Column 3 is table name
				tables.add(results.getString(3));
			}
			while (retry) {
				retry = false;
				List<String> successfulRemovals = new LinkedList<>();
				for (String tableName : tables) {
					try {
						handle.createScript("DROP TABLE " + tableName).execute();
						successfulRemovals.add(tableName);
					} catch (DBIException exception) {
						retry = true;
					}
				}
				tables.removeAll(successfulRemovals);
			}
		} catch (SQLException exception) {
			LOGGER.error("Could not drop all tables", exception);
			System.exit(1);
		}

	}

	/**
	 * Tears down the database connection and resets all related internal fields to null.
	 */
	public void tearDownDb() {
		handle.close();
		dbi = null;
		handle = null;
	}

	/**
	 * Creates a new thread on which to run the SingularityServer instance. Uses the DB instance set up by the
	 * {@link #setupDbConnection()} method and the temporary configuration file set up by the
	 * {@link #setupConfiguration()}. Then waits for the server to start by attempting to get a health check from the
	 * admin port. Performs a health check using a linear back off (25ms * attempt number).
	 * 
	 * @require {@link #setupConfiguration()} and {@link #setupDbConnection()} have both been run (in that order).
	 * @throws IllegalStateException
	 *             Thrown if there already exists a server in this {@link TestEnvironment} which hasn't been stopped by
	 *             {@link #stopServer()} or if the healthchecks could not verify the server has started.
	 * 
	 */
	public void setupServer() {
		if (serverThread != null) {
			throw new IllegalStateException("This test environment already has an active server thread");
		}

		serverThread = new Thread(new Runnable() {

			@Override
			public void run() {
				activeServer = new SingularityServer();
				try {
					activeServer.run("server", tempConfigurationFileName);
				} catch (Exception exception) {
					throw new RuntimeException(exception);
				}
			}
		});
		serverThread.start();
		int adminPort = jsonConfiguration.get("server").withArray("adminConnectors").get(0).get("port").asInt();

		SingularityRestClient restClient = new SingularityRestClient(ServerConstants.LOCAL_HOST, 0);
		LOGGER.info("Waiting for server thread to start");
		int attempts = 0;
		while (!restClient.isServerAlive(adminPort)) {
			try {
				Thread.sleep((long) attempts * 25);
			} catch (InterruptedException exception) {
				LOGGER.error("The sleep got interrupted while waiting for server to start", exception);
				Thread.currentThread().interrupt();
			}
			if (attempts > 25) {
				throw new IllegalStateException("Could not verify server is alive");
			}
			attempts++;
		}
		LOGGER.info("Verified server is up");

	}

	/**
	 * Attempts to stop the server that is running if one has been started. Checks whether the thread has stopped by
	 * checking if it has entered state: [{@link State#TERMINATED}] using a linear back off approach (25ms * attempt
	 * number)
	 * 
	 * @throws IllegalStateException
	 *             Thrown if the thread does not enter the {@link State#TERMINATED} state after 25 checks.
	 */
	public void stopServer() {
		if (serverThread != null) {
			activeServer.stopAllServices();
			LOGGER.info("Waiting for server thread to terminate.");
			int attempts = 0;
			while (serverThread.getState() != State.TERMINATED) {
				try {
					Thread.sleep((long) attempts * 25);
				} catch (InterruptedException exception) {
					LOGGER.error("The sleep got interrupted while waiting for server to stop", exception);
					Thread.currentThread().interrupt();
				}
				if (attempts > 25) {
					throw new IllegalStateException("Thread needs to stop but won't. ");
				}
				attempts++;
			}
			LOGGER.info("Verified server thread has terminated");
		}
		serverThread = null;
	}

	/**
	 * Gets the DBI that is encapsulated by this test environment. Often used for the {@link DBI#onDemand(Class)} method
	 * used to create a DAO from an interface with {@link SqlQuery}, {@link SqlUpdate}, etc. annotations on the methods.
	 * 
	 * @return The DBI instance encapsulated in the class
	 * @throws NullPointerException
	 *             Thrown if the DBI instance in the class is null as {@link #setupDbConnection()} hasn't been run, or
	 *             {@link #tearDownDb()} has been run.
	 */
	public DBI getDbi() {
		if (dbi == null) {
			throw new NullPointerException("Database must be set up before DBI can be accessed");
		}

		return dbi;
	}

	/**
	 * Gets the temporary server configuration that's been created by this test environment.
	 * 
	 * @return The Server configuration that would get applied if {@link #setupServer()} is run. Is returned as
	 *         {@link ServerConfiguration} object.
	 * @throws NullPointerException
	 *             Thrown if {@link #setupConfiguration()} hasn't been run to setup the configuration files.
	 */
	public ServerConfiguration getConfiguration() {
		if (configuration == null) {
			throw new NullPointerException(
					"Confguration must be set up before the ServerConfiguration can be accessed");
		}
		return configuration;
	}

	/**
	 * Gets the temporary server configuration that's been created by this test environment.
	 * 
	 * @return The Server configuration that would get applied if {@link #setupServer()} is run. Is returned as
	 *         {@link JsonNode} object.
	 * @throws NullPointerException
	 *             Thrown if {@link #setupConfiguration()} hasn't been run to setup the configuration files.
	 */
	public JsonNode getJsonConfiguration() {
		if (jsonConfiguration == null) {
			throw new NullPointerException("Confguration must be set up before the JsonConfiguration can be accessed");
		}
		return jsonConfiguration;
	}
}
