package uq.deco2800.singularity.integration.test.ducktales;

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

import uq.deco2800.singularity.clients.ducktales.DucktalesClient;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.Trade;
import uq.deco2800.singularity.common.representations.TradeRequest;
import uq.deco2800.singularity.common.representations.TradeResponse;
import uq.deco2800.singularity.common.representations.User;
import uq.deco2800.singularity.integration.test.util.TestEnvironment;
import uq.deco2800.singularity.server.trade.TradeDao;
import uq.deco2800.singularity.server.trade.TradeRequestDao;
import uq.deco2800.singularity.server.trade.TradeResponseDao;
import uq.deco2800.singularity.server.user.UserDao;

public class DucktalesClientTest {
	
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
	 * A test for getting a trade request through a user id
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	@Test
	public void getUserTradeRequestTest() throws JsonProcessingException, WebApplicationException {
		DucktalesClient client = new DucktalesClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		TradeDao tradeDao = dbi.onDemand(TradeDao.class);
		
		String collectionId = "1";
		String itemId = "1";
		String itemAmount = "1";
		Trade insertedTrade = new Trade(collectionId, itemId, itemAmount);
		
		tradeDao.insert(insertedTrade);
		
		// Add a user who will not be tested
		User notTestedUser = 
				new User("NotTested", "fnameOther", "mnameOther", 
						"lnameOther", "passwordOther");
				
		client.createUser(notTestedUser);
		
		// Add the user who will be the main user in the test
		String username = "Username1";
        String password = "Password1";
		
		User testedUser = 
				new User(username, "fname", "mname", "lname", password);
		testedUser = client.createUser(testedUser);
		
		// Set up the client credentials
		client.setupCredentials(username, password);
		
		// Get the tradeRequests for that user (There should be none)
		List<TradeRequest> userTrades = null;
		
		try {
			userTrades = client.getTradesMadeByUser(testedUser);
		} catch(IOException exception) {
			Assert.fail("Should not have thrown an IO exception.");
		}
		
		Assert.assertTrue("userTrades should be an empty list, not null", 
				userTrades != null);
		
		// Add a tradeRequest with the tested user's user id
		String offeredCollectionId = "1";
		String userId = testedUser.getUserId();
		
		TradeRequest insertedTradeRequest = 
				new TradeRequest(offeredCollectionId, userId);
		
		insertedTradeRequest = client.createTrade(insertedTradeRequest);
		
		// Check that there is now one trade for the tested user
		try {
			userTrades = client.getTradesMadeByUser(testedUser);
		} catch(IOException exception) {
			Assert.fail("Should not have thrown an IO exception.");
		}
		
		Assert.assertTrue("userTrades should contain one trade", 
				userTrades.size() == 1);
		
		Assert.assertTrue("The trade in userTrades should no be null", 
				userTrades.get(0) != null);
		
		Assert.assertTrue("userTrade requestId should equal the inserted Trade\n" + 
				"userTrades.get(0).getRequestId() = " +userTrades.get(0).getRequestId() +
				"\ninsertedTradeRequest.getRequestId() = " +insertedTradeRequest.getRequestId(),
				userTrades.get(0).getRequestId().equals(insertedTradeRequest.getRequestId()));
		
		Assert.assertTrue("userTrade collectionId should equal the inserted Trade\n" + 
				"userTrades.get(0).getOfferedCollectionId() = " + userTrades.get(0).getOfferedCollectionId() +
				"\ninsertedTradeRequest.getOfferedCollectionId() = " + insertedTradeRequest.getOfferedCollectionId(),
				userTrades.get(0).getOfferedCollectionId().equals(insertedTradeRequest.getOfferedCollectionId()));
		
		Assert.assertTrue("userTrade userId should equal the inserted Trade\n" + 
				"userTrades.get(0).getUserId() = " + userTrades.get(0).getUserId() +
				"\ninsertedTradeRequest.getUserId() = " + insertedTradeRequest.getUserId(), 
				userTrades.get(0).getUserId().equals(insertedTradeRequest.getUserId()));
		
		Assert.assertTrue("userTrades should contain the inserted Trade", 
				userTrades.contains(insertedTradeRequest));
		
	}
	/**
	 * Test for getting a trade request through a non existent user 
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	
	@Test
	public void getTradeRequestNotMadeByUserTest() throws JsonProcessingException, WebApplicationException {
DucktalesClient client = new DucktalesClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		TradeDao tradeDao = dbi.onDemand(TradeDao.class);
		
		String collectionId = "1";
		String itemId = "1";
		String itemAmount = "1";
		Trade insertedTrade = new Trade(collectionId, itemId, itemAmount);
		
		tradeDao.insert(insertedTrade);
		
		// Add a user who will not be tested
		User notTestedUser = 
				new User("NotTested", "fnameOther", "mnameOther", 
						"lnameOther", "passwordOther");
				
		client.createUser(notTestedUser);
		
		// Add the user who will be the main user in the test
		String username = "Username1";
        String password = "Password1";
		
		User testedUser = 
				new User(username, "fname", "mname", "lname", password);
		testedUser = client.createUser(testedUser);
		
		// Set up the client credentials
		client.setupCredentials(username, password);
		
		// Get the tradeRequests for that user (There should be none)
		List<TradeRequest> userTrades = null;
		
		try {
			userTrades = client.getTradesNotMadeByUser(testedUser);
		} catch(IOException exception) {
			Assert.fail("Should not have thrown an IO exception.");
		}
		
		Assert.assertTrue("userTrades should be an empty list, not null", 
				userTrades != null);
		
		// Add a tradeRequest with the tested user's user id
		String offeredCollectionId = "1";
		String userId = testedUser.getUserId();
		
		TradeRequest insertedTradeRequest = 
				new TradeRequest(offeredCollectionId, userId);
		
		insertedTradeRequest = client.createTrade(insertedTradeRequest);
		
		// Check that still no trades are returned
		try {
			userTrades = client.getTradesNotMadeByUser(testedUser);
		} catch(IOException exception) {
			Assert.fail("Should not have thrown an IO exception.");
		}
		
		Assert.assertTrue("userTrades should not contain any trades", 
				userTrades.size() == 0);
		
		// Add a tradeRequest by the other user
		String collectionIdTwo = "2";
		
		Trade insertedTradeTwo = new Trade(collectionIdTwo, itemId, itemAmount);
		
		tradeDao.insert(insertedTradeTwo);
		
		// Add a tradeRequest with the tested user's user id
		String offeredCollectionIdTwo = "2";
		String userIdTwo = notTestedUser.getUserId();
		
		TradeRequest insertedTradeRequestTwo = 
				new TradeRequest(offeredCollectionIdTwo, userIdTwo);
		
		insertedTradeRequest = client.createTrade(insertedTradeRequestTwo);
		
		// Check that one trade is now returned.
		try {
			userTrades = client.getTradesNotMadeByUser(testedUser);
		} catch(IOException exception) {
			Assert.fail("Should not have thrown an IO exception.");
		}
		
		Assert.assertTrue("userTrades should contain one trade", 
				userTrades.size() == 1);
		
		Assert.assertTrue("userTrades should contain the inserted Trade", 
				userTrades.contains(insertedTradeRequestTwo));
	}
	/**
	 * Test to grab a trade response through a user id
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	
	@Test
	public void getTradeResponseTest() throws JsonProcessingException, WebApplicationException {
		DucktalesClient client = new DucktalesClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
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
