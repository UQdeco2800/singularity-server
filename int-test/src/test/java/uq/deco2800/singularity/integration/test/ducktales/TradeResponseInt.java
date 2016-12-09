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

public class TradeResponseInt {
	
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
	 * Simple test that checks that the trade response table is empty on start.
	 */
	@Test
	public void emptyDbTest() {
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		TradeResponseDao tradeResponseDao = dbi.onDemand(TradeResponseDao.class);
		int expected = 0;
		int actual = tradeResponseDao.getAll().size();
		Assert.assertEquals("Trade Respons table should be empty to start", expected, actual);
	}
	
	@Test
	public void constructTradeRequest() {
		
		TradeResponse tradeResponse = new TradeResponse();
		
		Assert.assertTrue("tradeResponse should not be null", 
				tradeResponse != null);
	}
	
	/**
	 * Simple test that inserts a trade response through the tradeResponse Dao
	 * also to check if the foreign key constraints works 
	 */
	@Test
	public void insertTradeResponse() {
		
		//adds a user into the user table 
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

		Set<String> insertedTrades = new HashSet<String>();
		DBI dbi1 = TEST_ENVIRONMENT.getDbi();
		TradeDao tradeDao = dbi1.onDemand(TradeDao.class);
		
		String itemId = "1";
		String collectionId = "1";
		String itemAmount = "1";
		Trade insertedTrade = new Trade(collectionId, itemId, itemAmount);
		
		
		tradeDao.insert(insertedTrade);
		
		int expectedSize1 = 1;
		
		List<Trade> allTrades = tradeDao.getAll();
		
		int actualSize1 = allTrades.size();
		
		Assert.assertEquals("Trade table should have 1 trade", expectedSize1, actualSize1);
		
		//add the trade response into the trade response table
		Set<String> insertedTradeResponse = new HashSet<String>();
		DBI dbi2 = TEST_ENVIRONMENT.getDbi();
		TradeResponseDao tradeResponseDao = dbi2.onDemand(TradeResponseDao.class);
		
		String userId = "1";
		TradeResponse insertedResponse = new TradeResponse(userId,itemId,collectionId);
		
		tradeResponseDao.insert(insertedResponse);
		
		int expectedSize2 = 1;
		
		List<TradeResponse> allTrades1 = tradeResponseDao.getAll();
		
		int actualSize2 = allTrades1.size();
		
		Assert.assertEquals("Trade Response table should have 1 Request made", expectedSize2, actualSize2);
		
	}
	/**
	 * Test for the trade response equals
	 */
	@Test
	public void equalsTest() {
		
		String itemIdOne = "1";
		String collectionIdOne = "1";
		String userIdOne = "1";
		TradeResponse firstResponse = 
				new TradeResponse(itemIdOne,userIdOne,collectionIdOne);
		
		Assert.assertTrue("Responses should not equal null", 
				firstResponse != null);
		
		TradeResponse equalResponse = 
				new TradeResponse(itemIdOne,userIdOne,collectionIdOne);
		
		Assert.assertTrue("Responses should be equal if they have the same " + 
				"requestId and collectionId", 
				firstResponse.equals(equalResponse));
		
		String itemIdTwo = "2";
		String collectionIdTwo = "2";
		String userIdTwo = "2";
		
		TradeResponse itemIdDifferentResponse = 
				new TradeResponse(itemIdTwo,userIdOne,collectionIdOne);
		
		Assert.assertTrue("Responses should not equal if itemIds are " + 
				"different", !firstResponse.equals(itemIdDifferentResponse));
		
		TradeResponse collectionIdDifferentResponse = 
				new TradeResponse(itemIdOne,userIdOne,collectionIdTwo);
		
		Assert.assertTrue("Responses should not equal if collectionIds are " + 
				"different", 
				!firstResponse.equals(collectionIdDifferentResponse));
		
		TradeResponse userIdDifferentResponse = 
				new TradeResponse(itemIdOne,userIdTwo,collectionIdOne);
		
		Assert.assertTrue("Responses should not equal if collectionIds are " + 
				"different", 
				!firstResponse.equals(userIdDifferentResponse));
		
		TradeResponse idsDifferentResponse = 
				new TradeResponse(itemIdTwo,userIdTwo,collectionIdTwo);
		
		Assert.assertTrue("Requests should not equal if requestIds and " + ""
				+ "collectionIds are different", 
				!firstResponse.equals(idsDifferentResponse));
	}
	/**
	 * 
	 * Hashcode testing of the trade response 
	 */
	@Test
	public void hashCodeTest() {
		
		String itemIdOne = "1";
		String collectionIdOne = "1";
		String userIdOne = "1";
		TradeResponse firstResponse = 
				new TradeResponse(itemIdOne,userIdOne,collectionIdOne);
		
		TradeResponse equalResponse = 
				new TradeResponse(itemIdOne,userIdOne,collectionIdOne);
		
		Assert.assertTrue("Response hashcodes should be equal if they have the same " + 
				"requestId and collectionId", 
				firstResponse.hashCode() == equalResponse.hashCode());
		
		String itemIdTwo = "2";
		String collectionIdTwo = "2";
		String userIdTwo = "2";
		
		TradeResponse itemIdDifferentResponse = 
				new TradeResponse(itemIdTwo,userIdOne,collectionIdOne);
		
		Assert.assertTrue("Response hashcodes should not equal if itemIds are " + 
				"different", 
				firstResponse.hashCode() != itemIdDifferentResponse.hashCode());
		
		TradeResponse collectionIdDifferentResponse = 
				new TradeResponse(itemIdOne,userIdOne,collectionIdTwo);
		
		Assert.assertTrue("Response hashcodes should not equal if collectionIds are " + 
				"different", 
				firstResponse.hashCode() != 
				collectionIdDifferentResponse.hashCode());
		
		TradeResponse userIdDifferentResponse = 
				new TradeResponse(itemIdOne,userIdTwo,collectionIdOne);
		
		Assert.assertTrue("Response hashcodes should not equal if collectionIds are " + 
				"different", 
				firstResponse.hashCode() != userIdDifferentResponse.hashCode());
		
		TradeResponse idsDifferentResponse = 
				new TradeResponse(itemIdTwo,userIdTwo,collectionIdTwo);
		
		Assert.assertTrue("Response hashcodes should not equal if requestIds and " + ""
				+ "collectionIds are different", 
				firstResponse.hashCode() != idsDifferentResponse.hashCode());
	}
	
	

}
