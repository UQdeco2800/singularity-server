package uq.deco2800.singularity.integration.test.ducktales;

import java.util.List;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
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
import uq.deco2800.singularity.server.user.UserDao;

public class TradeTest {
	
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
	 * Test to check that  if the trade is not empty, able to grab 
	 * trade information 
	 * 
	 */
	@Test
	public void constructTrade() {
		
		Trade trade = new Trade();
		
		Assert.assertTrue("trade should not be null", 
				trade != null);
		
	}
	
	/**
	 * simple test that inserts a trade into the trade table
	 * 
	 */
	
	@Test
	public void insetTradeTest() {
		Set<String> insertedTrades = new HashSet<String>();
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		TradeDao tradeDao = dbi.onDemand(TradeDao.class);
		
		String collectionId = "1";
		String itemId = "1";
		String itemAmount = "1";
		Trade insertedTrade = new Trade(collectionId, itemId, itemAmount);
		
		
		tradeDao.insert(insertedTrade);
		
		int expectedSize = 1;
		
		List<Trade> allTrades = tradeDao.getAll();
		
		int actualSize = allTrades.size();
		
		Assert.assertEquals("Trade table should have 1 trade", expectedSize, actualSize);
		Trade retrievedTrade = allTrades.get(0);
		insertedTrades.add(retrievedTrade.getcollectionId());
		Assert.assertEquals("Inserted trade should equal retrieved trade", insertedTrade, retrievedTrade);
		
	}
	/**
	 * hash code testing of the trade resource 
	 */
	@Test
	public void hashCodeTest() {
		
		String collectionIdOne = "1";
		String itemIdOne = "1";
		String itemAmountOne = "1";
		Trade firstResponse = 
				new Trade(itemIdOne,itemAmountOne,collectionIdOne);
		
		Trade equalResponse = 
				new Trade(itemIdOne,itemAmountOne,collectionIdOne);
		
		Assert.assertTrue("Response hashcodes should be equal if they have the same " + 
				"requestId and collectionId", 
				firstResponse.hashCode() == equalResponse.hashCode());
		
		String collectionIdTwo = "2";
		String itemIdTwo = "2";
		String itemAmountTwo = "2";
		
		Trade itemIdDifferentResponse = 
				new Trade(itemIdTwo,itemAmountOne,collectionIdOne);
		
		Assert.assertTrue("Response hashcodes should not equal if itemIds are " + 
				"different", 
				firstResponse.hashCode() != itemIdDifferentResponse.hashCode());
		
		Trade collectionIdDifferentResponse = 
				new Trade(itemIdOne,itemAmountOne,collectionIdTwo);
		
		Assert.assertTrue("Response hashcodes should not equal if collectionIds are " + 
				"different", 
				firstResponse.hashCode() != 
				collectionIdDifferentResponse.hashCode());
		
		Trade userIdDifferentResponse = 
				new Trade(itemIdOne,itemAmountTwo,collectionIdOne);
		
		Assert.assertTrue("Response hashcodes should not equal if collectionIds are " + 
				"different", 
				firstResponse.hashCode() != userIdDifferentResponse.hashCode());
		
		Trade idsDifferentResponse = 
				new Trade(itemIdTwo,itemAmountTwo,collectionIdTwo);
		
		Assert.assertTrue("Trade hashcodes should not equal if requestIds and " + ""
				+ "collectionIds are different", 
				firstResponse.hashCode() != idsDifferentResponse.hashCode());
	}
	/**
	 * test for the trade resource equals
	 */
	@Test
	public void equalsTest() {
		
		String collectionIdOne = "1";
		String itemIdOne = "1";
		String itemAmountOne = "1";
		Trade firstTrade = 
				new Trade(collectionIdOne, itemIdOne, itemAmountOne);
		
		Assert.assertTrue("Trade should not equal null", 
				firstTrade != null);
		
		Trade equalTrade = 
				new Trade(collectionIdOne, itemIdOne, itemAmountOne);
		
		Assert.assertTrue("Requests should be equal if they have the same " + 
				"requestId and collectionId", 
				firstTrade.equals(equalTrade));
		
		String collectionIdTwo = "2";
		String itemIdTwo = "2";
		String itemAmountTwo = "2";
		
		Trade tradeIdDifferentRequest = 
				new Trade(collectionIdTwo, itemIdOne, itemAmountOne);
		
		Assert.assertTrue("Trade should not equal if collectionIds are " + 
				"different", !firstTrade.equals(tradeIdDifferentRequest));
		
		Trade collectionIdDifferentRequest = 
				new Trade(collectionIdOne, itemIdTwo, itemAmountOne);
		
		Assert.assertTrue("Trade should not equal if collectionIds are " + 
				"different", 
				!firstTrade.equals(collectionIdDifferentRequest));
		
		
		Trade idsDifferentRequest = 
				new Trade(collectionIdTwo, itemIdTwo, itemAmountOne);
		
		Assert.assertTrue("Trade should not equal if requestIds and " + ""
				+ "collectionIds are different", 
				!firstTrade.equals(idsDifferentRequest));
		
		Trade itemAmountDifferent = 
				new Trade(collectionIdTwo,itemIdTwo, itemAmountTwo);
		
		Assert.assertTrue("Trade should not equal if itemAmounts are "+
		"different", !firstTrade.equals(itemAmountDifferent));
	}
	/**
	 * test getting a collection through a collection Id
	 */
	@Test
	public void clientGetCollectionTest() {
		DucktalesClient client = new DucktalesClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		try {
			client.getTradeInformationById("");
			Assert.fail("Should have gotten a web application exception as"
					+ " DB should be empty and ID shouldn't correlate to a Trade.");
		} catch (WebApplicationException exception) {
			Response response = exception.getResponse();
			Assert.assertNotNull("Exception should encapsulate the response.", response);
			Assert.assertEquals("Status response should be bad request", Status.BAD_REQUEST.getStatusCode(),
					response.getStatus());
		}

		try {
			client.getTradeInformationById(UUID.randomUUID().toString());
			Assert.fail("Should have gotten a web application exception as"
					+ " DB should be empty and ID shouldn't correlate to a user.");
		} catch (WebApplicationException exception) {
			Response response = exception.getResponse();
			Assert.assertNotNull("Exception should encapsulate the response.", response);
			Assert.assertEquals("Status response should be not found", Status.NOT_FOUND.getStatusCode(),
					response.getStatus());
		}
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		TradeDao tradeDao = dbi.onDemand(TradeDao.class);
		String collectionId = "1";
		String itemId = "1";
		String itemAmount = "1";
		Trade insertedTrade = new Trade(collectionId, itemId, itemAmount);
		
		tradeDao.insert(insertedTrade);
		Trade tradeById = client.getTradeInformationById(insertedTrade.getcollectionId());
		Assert.assertEquals("Getting a user by ID should be retrieve the same object as the inserted object", insertedTrade,
				tradeById);
		
	}
	/**
	 * Test to make a new trade through the client 
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	
	@Test
	public void clientNewTradeTest() throws JsonProcessingException, WebApplicationException { 
		DucktalesClient client = new DucktalesClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		String collectionId = "1";
		String itemId = "1";
		String itemAmount = "1";
		Trade insertedTrade = new Trade(collectionId, itemId, itemAmount);
		client.createCollection(insertedTrade);
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		TradeDao tradeDao = dbi.onDemand(TradeDao.class);
		Trade tradeFromDao = tradeDao.findById(insertedTrade.getcollectionId());
		Assert.assertEquals("Inserted trade does not match stored trade", tradeFromDao, insertedTrade);

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
