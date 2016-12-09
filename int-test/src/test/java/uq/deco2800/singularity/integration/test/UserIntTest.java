package uq.deco2800.singularity.integration.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.User;
import uq.deco2800.singularity.integration.test.util.TestEnvironment;
import uq.deco2800.singularity.server.user.UserDao;

/**
 * 
 */

/**
 * @author dloetscher
 *
 */
public class UserIntTest {

	/**
	 * The test environment used in this test for database and server/client testing.
	 */
	private static final TestEnvironment TEST_ENVIRONMENT = new TestEnvironment();

	/**
	 * Sets up a temporary configuration yaml file with randomised ports. <br>
	 * <br>
	 * Sets the DB used to be a in-memory database which is unique to this test run. This DB will obviously be cleared
	 * when the process exits. <br>
	 * <br>
	 * Starts a new SingularityServer instance using the randomised ports and in-memory DB.
	 * 
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	@BeforeClass
	public static void setupClass() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		TEST_ENVIRONMENT.setupConfiguration();
		TEST_ENVIRONMENT.setupDbConnection();
		TEST_ENVIRONMENT.migrateDb();
		TEST_ENVIRONMENT.setupServer();
	}

	/**
	 * Shuts down the server then shuts down the connection to the in-memory database
	 */
	@AfterClass
	public static void tearDownClass() {
		TEST_ENVIRONMENT.stopServer();
		TEST_ENVIRONMENT.tearDownDb();
	}

	/**
	 * Clears the database of all tables, then iterates through all the SQL files and re-applies them to have a fresh
	 * DB.
	 * @throws URISyntaxException 
	 */
	@After
	public void tearDown() throws URISyntaxException {
		TEST_ENVIRONMENT.emptyDb();
		TEST_ENVIRONMENT.migrateDb();
	}

	/**
	 * Simple test that checks that the user table is empty on start.
	 */
	@Test
	public void emptyDbTest() {
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		UserDao userDao = dbi.onDemand(UserDao.class);
		int expected = 0;
		int actual = userDao.getAll().size();
		Assert.assertEquals("User table should be empty to start", expected, actual);
	}

	/**
	 * Simple test that inserts 2 users directly through the UserDao and checks that the retrieved data matches the
	 * inserted data.
	 */
	@Test
	public void insertUserTest() {
		Set<String> insertedIds = new HashSet<String>();
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		UserDao userDao = dbi.onDemand(UserDao.class);
		User insertedUser = new User("test", "fname", "mname", "lname", "password");
		insertedUser.setUserId("1");
		insertedUser.setSalt("salt");
		userDao.insert(insertedUser);
		int expectedSize = 1;
		List<User> allUsers = userDao.getAll();
		int actualSize = allUsers.size();
		Assert.assertEquals("User table should be have 1 user", expectedSize, actualSize);
		User retrievedUser = allUsers.get(0);
		insertedIds.add(retrievedUser.getUserId());
		Assert.assertEquals("Inserted user should equal retrieved User", insertedUser, retrievedUser);

		User insertedUser2 = new User("test2", "fname2", "mname2", "lname2", "password2");
		insertedUser2.setUserId("2");
		insertedUser2.setSalt("salt");
		userDao.insert(insertedUser2);
		expectedSize = 2;
		allUsers = userDao.getAll();
		actualSize = allUsers.size();
		Assert.assertEquals("User table should be have 1 user", expectedSize, actualSize);
		boolean foundInsertedUser2 = false;
		for (User user : allUsers) {
			insertedIds.add(user.getUserId());
			if (user.equals(insertedUser2)) { // first user
				foundInsertedUser2 = true;
			}
		}
		Assert.assertTrue("The second inserted user could not be found when iterating through all users",
				foundInsertedUser2);
		Assert.assertEquals("The number of unique ids must be the number of inserted users (2)", 2, insertedIds.size());
	}

	/**
	 * Slightly more advanced test which uses the SingularityRestClient. Creates a User and inserts the user using the
	 * rest client. Then uses the UserDao to verify that the user in the DB matches the user that should have been
	 * inserted.
	 * 
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	@Test
	public void clientNewUserTest() throws JsonProcessingException, WebApplicationException {
		SingularityRestClient client = new SingularityRestClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		User insertedUser = new User("test", "fname", "mname", "lname", "password");
		client.createUser(insertedUser);
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		UserDao userDao = dbi.onDemand(UserDao.class);
		User userFromDao = userDao.findById(insertedUser.getUserId());
		userFromDao.clearPasswords();
		insertedUser.clearPasswords();
		Assert.assertEquals("Inserted user does not match stored user", userFromDao, insertedUser);

		// Attempt to insert same user. Should get error.
		insertedUser.setUserId(null);
		try {
			client.createUser(insertedUser);
			Assert.fail("Should have thrown an exception.");
		} catch (WebApplicationException exception) {
			Response response = exception.getResponse();
			Assert.assertNotNull("Exception should encapsulate the response.", response);
			Assert.assertEquals("Status response should be conflict", Status.CONFLICT.getStatusCode(),
					response.getStatus());
		}
	}

	@Test
	public void clientGetUserTest() {
		SingularityRestClient client = new SingularityRestClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		try {
			client.getUserInformationById("");
			Assert.fail("Should have gotten a web application exception as"
					+ " DB should be empty and ID shouldn't correlate to a user.");
		} catch (WebApplicationException exception) {
			Response response = exception.getResponse();
			Assert.assertNotNull("Exception should encapsulate the response.", response);
			Assert.assertEquals("Status response should be bad request", Status.BAD_REQUEST.getStatusCode(),
					response.getStatus());
		}

		try {
			client.getUserInformationById(UUID.randomUUID().toString());
			Assert.fail("Should have gotten a web application exception as"
					+ " DB should be empty and ID shouldn't correlate to a user.");
		} catch (WebApplicationException exception) {
			Response response = exception.getResponse();
			Assert.assertNotNull("Exception should encapsulate the response.", response);
			Assert.assertEquals("Status response should be not found", Status.NOT_FOUND.getStatusCode(),
					response.getStatus());
		}

		try {
			client.getUserInformationByUserName("foo");
			Assert.fail("Should have gotten a web application exception as"
					+ " DB should be empty and ID shouldn't correlate to a user.");
		} catch (WebApplicationException exception) {
			Response response = exception.getResponse();
			Assert.assertNotNull("Exception should encapsulate the response.", response);
			Assert.assertEquals("Status response should be conflict", Status.NOT_FOUND.getStatusCode(),
					response.getStatus());
		}

		DBI dbi = TEST_ENVIRONMENT.getDbi();
		UserDao userDao = dbi.onDemand(UserDao.class);
		User newUser = new User("jsteel", "Jim", "Iron", "Steel", "jsteelisabaws");
		newUser.setUserId(UUID.randomUUID().toString());
		newUser.setSalt("salty");
		userDao.insert(newUser);
		User userById = client.getUserInformationById(newUser.getUserId());
		User userByName = client.getUserInformationByUserName(newUser.getUsername());
		newUser.clearPasswords();
		Assert.assertEquals("Getting a user by ID should be retrieve the same object as the inserted object", newUser,
				userById);
		Assert.assertEquals("Getting a user by username should be retrieve the same object as the inserted object",
				newUser, userByName);
	}

	/**
	 * Helper method which retrieves the port the SingularityServer is running on.
	 * 
	 * @return A valid port from 1 to 65535
	 */
	private int getRestApplicationPort() {
		JsonNode jsonConfiguration = TEST_ENVIRONMENT.getJsonConfiguration();
		return jsonConfiguration.get("server").withArray("applicationConnectors").get(0).get("port").asInt();
	}
}
