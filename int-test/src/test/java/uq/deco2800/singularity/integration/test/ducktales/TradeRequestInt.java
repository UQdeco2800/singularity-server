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
import uq.deco2800.singularity.common.representations.User;
import uq.deco2800.singularity.integration.test.util.TestEnvironment;
import uq.deco2800.singularity.server.trade.TradeDao;
import uq.deco2800.singularity.server.trade.TradeRequestDao;
import uq.deco2800.singularity.server.user.UserDao;

/**
 * 
 * @author Gregory
 *
 */

public class TradeRequestInt {
	
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
	 * 
	 * checks the trade request table is empty on start.
	 */
	@Test
	public void emptyTRDbTest() {
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		TradeRequestDao tradeRequestDao = dbi.onDemand(TradeRequestDao.class);
		int expected = 0;
		int actual = tradeRequestDao.getAll().size();
		Assert.assertEquals("User table should be empty to start", expected, actual);
	}
	/**
	 * test to construct a trade request
	 */
	@Test
	public void constructTradeRequest() {
		
		TradeRequest tradeRequest = new TradeRequest();
		
		Assert.assertTrue("tradeRequest should not be null", 
				tradeRequest != null);
		
		Assert.assertFalse("tradeRequest is not null", tradeRequest == null);
	}
	/**
	 * a simple test to inserting some trade request
	 */
	@Test
	public void insertTradeRequest() {
		
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
		
		Set<String> insertedTradeRequest = new HashSet<String>();
		DBI dbi1 = TEST_ENVIRONMENT.getDbi();
		TradeRequestDao tradeRequestDao = dbi1.onDemand(TradeRequestDao.class);
		
		String offeredCollectionId = "1";
		String userId = "1";
		String requestId = "1";
		TradeRequest insertedRequest = new TradeRequest(offeredCollectionId, 
				userId);
		
		insertedRequest = insertedRequest.setRequestId(requestId);
		
		tradeRequestDao.insert(insertedRequest);
		
		int expectedSize1 = 1;
		
		List<TradeRequest> allTrades1 = tradeRequestDao.getAll();
		
		int actualSize1 = allTrades1.size();
		
		Assert.assertEquals("Trade Request table should have 1 Request made", expectedSize1, actualSize1);
		
	}
	/**
	 * testing the trade request equals 
	 * 
	 */
	@Test
	public void equalsTest() {
		
		String offeredCollectionIdOne = "1";
		String userIdOne = "1";
		
		TradeRequest firstRequest = 
				new TradeRequest(offeredCollectionIdOne, userIdOne);
		
		String requestIdOne = "1";
		firstRequest.setRequestId(requestIdOne);
		
		Assert.assertTrue("Request should not equal null", 
				firstRequest != null);
		
		TradeRequest equalRequest = 
				new TradeRequest(offeredCollectionIdOne, 
						userIdOne);
		
		equalRequest.setRequestId(requestIdOne);
		
		Assert.assertTrue("Requests should be equal if they have the same " + 
				"collectionId and user id", 
				firstRequest.equals(equalRequest));
		
		String offeredCollectionIdTwo = "2";
		String userIdTwo = "2";
		
		TradeRequest requestIdDifferentRequest = 
				new TradeRequest(offeredCollectionIdOne, 
						userIdTwo);
		
		String requestIdTwo = "2";
		requestIdDifferentRequest.setRequestId(requestIdTwo);
		
		Assert.assertTrue("Requests should not equal if requestIds are " + 
				"different", !firstRequest.equals(requestIdDifferentRequest));
		
		TradeRequest collectionIdDifferentRequest = 
				new TradeRequest(offeredCollectionIdTwo, 
						userIdOne);
		
		Assert.assertTrue("Requests should not equal if collectionIds are " + 
				"different", 
				!firstRequest.equals(collectionIdDifferentRequest));
		
		TradeRequest userIdDifferentRequest = 
				new TradeRequest(offeredCollectionIdOne, 
						userIdTwo);
		
		Assert.assertTrue("Requests should not equal if userIds are " + 
				"different", 
				!firstRequest.equals(userIdDifferentRequest));
		
		
		TradeRequest idsDifferentRequest = 
				new TradeRequest(offeredCollectionIdTwo, 
						userIdTwo);
		
		Assert.assertTrue("Requests should not equal if collectionIds and " + ""
				+ "userIds are different", 
				!firstRequest.equals(idsDifferentRequest));
	}
	/**
	 * testing the hashcode for trade request
	 */
	@Test
	public void hashCodeTest() {
		
		String offeredCollectionIdOne = "1";
		String userIdOne = "1";
		
		TradeRequest firstRequest = 
				new TradeRequest(offeredCollectionIdOne, 
						userIdOne);
		
		String requestIdOne = "1";
		firstRequest.setRequestId(requestIdOne);
		
		TradeRequest equalRequest = 
				new TradeRequest(offeredCollectionIdOne, 
						userIdOne);
		
		equalRequest.setRequestId(requestIdOne);
		
		Assert.assertTrue("Request hash codes should be equal if they " + 
				"have the same requestId, collectionId and userId", 
				firstRequest.hashCode() == equalRequest.hashCode());
		
		String requestIdTwo = "2";
		String offeredCollectionIdTwo = "2";
		String userIdTwo = "2";
		
		TradeRequest requestIdDifferentRequest = 
				new TradeRequest(offeredCollectionIdOne, 
						userIdOne);
		
		requestIdDifferentRequest.setRequestId(requestIdTwo);
		
		Assert.assertTrue("Request hash codes should not equal if " + 
				" requestIds are different", firstRequest.hashCode() != 
				requestIdDifferentRequest.hashCode());
		
		TradeRequest collectionIdDifferentRequest = 
				new TradeRequest(offeredCollectionIdTwo, 
						userIdTwo);
		
		collectionIdDifferentRequest.setRequestId(requestIdOne);
		
		Assert.assertTrue("Request hash codes should not equal if " + 
				"collectionIds are different", firstRequest.hashCode() != 
				collectionIdDifferentRequest.hashCode());
		
		
		TradeRequest idsDifferentRequest = 
				new TradeRequest(offeredCollectionIdTwo, 
						userIdTwo);
		
		idsDifferentRequest.setRequestId(requestIdTwo);
		
		Assert.assertTrue("Request hash codes should not equal if " +
				"requestIds, collectionIds and userIds are different", 
				firstRequest.hashCode() != idsDifferentRequest.hashCode());	
	}

}
