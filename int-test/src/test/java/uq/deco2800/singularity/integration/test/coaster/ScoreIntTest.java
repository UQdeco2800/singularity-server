/**
 * 
 */
package uq.deco2800.singularity.integration.test.coaster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import uq.deco2800.singularity.clients.coaster.CoasterClient;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.User;
import uq.deco2800.singularity.common.representations.coaster.Score;
import uq.deco2800.singularity.common.representations.coaster.ScoreType;
import uq.deco2800.singularity.integration.test.util.TestEnvironment;
import uq.deco2800.singularity.server.coaster.score.ScoreDao;
import uq.deco2800.singularity.server.user.UserDao;


/**
 * @author Kellie Lutze
 *
 */
public class ScoreIntTest {
	private static final TestEnvironment TEST_ENVIRONMENT = new TestEnvironment();
	
	@BeforeClass
	public static void setupSuit() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		TEST_ENVIRONMENT.setupConfiguration();
		TEST_ENVIRONMENT.setupDbConnection();
		TEST_ENVIRONMENT.migrateDb();
		TEST_ENVIRONMENT.setupServer();
	}
	
	@AfterClass 
	public static void tearDownSuit() {
		TEST_ENVIRONMENT.stopServer();
		TEST_ENVIRONMENT.tearDownDb();
	}
	
	/**
	 * Clear the db between tests
	 * @throws URISyntaxException 
	 */
	@After
	public void tearDown() throws URISyntaxException {
		TEST_ENVIRONMENT.emptyDb();
		TEST_ENVIRONMENT.migrateDb();
	}
	
	/**
	 * Insert Two scores into DB using Dao 
	 */
	@Test 
	public void insertHighScore() {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		UserDao userDao = dbi.onDemand(UserDao.class);
		User user = new User(username, "anonymous", null, "anonymous", password);
		user.setSalt("salty");
		user.setUserId("1");
		userDao.insert(user);
		Score expectedScore = new Score().setExperience("100").setTime().setUserId(user.getUserId()).setScoreId("1");
		scoreDao.insert(expectedScore);
		List<Score> scores = scoreDao.findHighestScoresByTypeExperience();
		assertTrue("A single score should be in residence", scores.size() == 1);
		
		Score expectedScore2 = new Score().setExperience("250").setTime().setUserId(user.getUserId()).setScoreId("2");
		scoreDao.insert(expectedScore2);
		scores = scoreDao.findHighestScoresByTypeExperience();
		assertTrue("Two scores should be in residence", scores.size() == 2);
	}
	
	/**
	 * Find existing score using score Id
	 */
	@Test 
	public void findById() {
		String username = "testUsername";
		String password = "testPassword";
		String scoreId1 = "1";
		String scoreId2 = "2";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		UserDao userDao = dbi.onDemand(UserDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password).setSalt("salty").setUserId("1");
		User user2 = new User(username + "2", "anonymous", null, "anonymous", password + "2").setSalt("salty").setUserId("2");
		userDao.insert(user);
		userDao.insert(user2);
		
		scoreDao.insert(new Score().setExperience("100").setTime().setUserId(user.getUserId()).setScoreId(scoreId1));
	    scoreDao.insert(new Score().setExperience("200").setTime().setUserId(user.getUserId()).setScoreId(scoreId2));

		Score score = scoreDao.findById(scoreId1);
		Score score2 = scoreDao.findById(scoreId2);
		
		assertEquals("Added score Id should match retrieved score ID", scoreId1, score.getScoreId());
		assertEquals("Added score Id should match retrieved score ID", scoreId2, score2.getScoreId());
	}
	
	/**
	 * Find Highest Scores by Id and Type Experience Using Dao
	 */
	@Test 
	public void findHighestScoresByExperienceAndId() {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		UserDao  userDao = dbi.onDemand(UserDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password).setSalt("salty").setUserId("1");
		User user2 = new User(username + "2", "anonymous", null, "anonymous", password + "2").setSalt("salty").setUserId("2");
		userDao.insert(user);
		userDao.insert(user2);
		
		List<Score> expectedScores = new ArrayList<Score>();
		scoreDao.insert(new Score().setExperience("100").setTime().setUserId(user.getUserId()).setScoreId("1")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
	    scoreDao.insert(new Score().setExperience("200").setTime().setUserId(user.getUserId()).setScoreId("2")
	    		.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("300").setTime().setUserId(user.getUserId()).setScoreId("3")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("400").setTime().setUserId(user.getUserId()).setScoreId("4")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("500").setTime().setUserId(user.getUserId()).setScoreId("5")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("600").setTime().setUserId(user.getUserId()).setScoreId("6")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("700").setTime().setUserId(user.getUserId()).setScoreId("7")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("800").setTime().setUserId(user.getUserId()).setScoreId("8")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("900").setTime().setUserId(user.getUserId()).setScoreId("9")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1000").setTime().setUserId(user.getUserId()).setScoreId("10")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1100").setTime().setUserId(user.getUserId()).setScoreId("11")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1200").setTime().setUserId(user.getUserId()).setScoreId("12")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		
		
		for (Score score : expectedScores) {
			scoreDao.insert(score);
		}
		scoreDao.insert(new Score().setExperience("1200").setTime().setUserId(user2.getUserId()).setScoreId("13")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));	
		List<Score> scores = scoreDao.findHighestScoresByExperienceAndId(user.getUserId());
		assertTrue("Ten scores should be returned", scores.size() == 10);
		
		Collections.sort(expectedScores, new ScoreComparatorExperience());

		int i = 0;
		for (Score score : scores) {
			assertEquals("Returned score list should match expected score list", expectedScores.get(i), score);
			i++;
		}
	}
	
	/**
	 * Find Highest Scores by Type Experience only Using Dao
	 */
	@Test 
	public void findHighestScoresByExperience() {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		UserDao  userDao = dbi.onDemand(UserDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password).setSalt("salty").setUserId("1");
		userDao.insert(user);
		
		List<Score> expectedScores = new ArrayList<Score>();
		scoreDao.insert(new Score().setExperience("100").setTime().setUserId(user.getUserId())
				.setScoreId("1").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
	    scoreDao.insert(new Score().setExperience("200").setTime().setUserId(user.getUserId())
	    		.setScoreId("2").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("300").setTime().setUserId(user.getUserId())
				.setScoreId("3").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("400").setTime().setUserId(user.getUserId())
				.setScoreId("4").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("500").setTime().setUserId(user.getUserId())
				.setScoreId("5").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("600").setTime().setUserId(user.getUserId())
				.setScoreId("6").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("700").setTime().setUserId(user.getUserId())
				.setScoreId("7").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("800").setTime().setUserId(user.getUserId())
				.setScoreId("8").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("900").setTime().setUserId(user.getUserId())
				.setScoreId("9").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1000").setTime().setUserId(user.getUserId())
				.setScoreId("10").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1100").setTime().setUserId(user.getUserId())
				.setScoreId("11").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1200").setTime().setUserId(user.getUserId())
				.setScoreId("12").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		
		for (Score score : expectedScores) {
			scoreDao.insert(score);
		}
		List<Score> scores = scoreDao.findHighestScoresByExperienceAndId(user.getUserId());
		
		Collections.sort(expectedScores, new ScoreComparatorExperience());
	
		int i = 0;
		for (Score score : scores) {
			assertEquals(expectedScores.get(i++), score);
		}
	}
	
	/**
	 * Create High score using CoasterClient 
	 * @throws JsonProcessingException 
	 * @throws WebApplicationException 
	 */
	@Test
	public void createHighScore() throws WebApplicationException, JsonProcessingException {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		CoasterClient client = new CoasterClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password);
		user = client.createUser(user);
		Score score = new Score().setExperience("100").setTime().setUserId(user.getUserId());
	    Score score2 = new Score().setExperience("200").setTime().setUserId(user.getUserId());
	    
	    client.setupCredentials(username, password);
	    Score createdScore = client.createHighScore(score);
	    assertNotNull("Created score should not have a null id", createdScore.getScoreId());
	    score.setScoreId(createdScore.getScoreId());
	    
	    Score actualScore = scoreDao.findById(score.getScoreId());
	    assertEquals("Added Score should equal expected retrieved score", score, actualScore);
	    
	    createdScore = client.createHighScore(score2);
	    score2.setScoreId(createdScore.getScoreId());
	    
	    actualScore = scoreDao.findById(score2.getScoreId());
	    assertEquals("Added Score should equal expected retrievd score", score2, actualScore);
	}

	
	/**
	 * Attempt to create invalid high score with negative experience using CoasterClient. 
	 * Expect Exception with response 400
	 * @throws WebApplicationException 
	 * @throws JsonProcessingException 
	 */
	@Test 
	public void createInvalidNegativeHighScore() throws JsonProcessingException, WebApplicationException {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		CoasterClient client = new CoasterClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		UserDao  userDao = dbi.onDemand(UserDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password);
		user = client.createUser(user);
		Score score = new Score().setExperience("-100").setTime().setUserId(user.getUserId());
	    Score score2 = new Score().setExperience("-999").setTime().setUserId(user.getUserId());

	    client.setupCredentials(username, password);
	    
		try {
			client.createHighScore(score);
			client.createHighScore(score2);
		} catch (WebApplicationException | JsonProcessingException e) {
			if (((WebApplicationException) e).getResponse().getStatus() == 400) {
				return;
			}
			fail("Failed to create score with Exception " + e);
		}
	}
	
	/**
	 * Attempt to create score with zero valued experience using CoasterClient 
	 * Expect Exception with response 400
	 * @throws WebApplicationException 
	 * @throws JsonProcessingException 
	 */
	@Test 
	public void createZeroInvalidHighScore() throws JsonProcessingException, WebApplicationException {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		CoasterClient client = new CoasterClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		UserDao  userDao = dbi.onDemand(UserDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password);
		user = client.createUser(user);
		Score score = new Score().setExperience("0").setTime().setUserId(user.getUserId());

	    client.setupCredentials(username, password);
	    
		try {
			client.createHighScore(score);
		} catch (WebApplicationException | JsonProcessingException e) {
			if (((WebApplicationException) e).getResponse().getStatus() == 400) {
				return;
			}
			fail("Failed to create score with Exception " + e);
		}
	}
	
	
	/**
	 * Retrieve high scores from database filtered by id and Type Experience using Coaster Client 
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	@Test
	public void getHighScoreByUserAndTypeExperience() throws JsonProcessingException, WebApplicationException {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		CoasterClient client = new CoasterClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password);
		User user2 = new User(username + "2", "anonymous", null, "anonymous", password + "2");
		user = client.createUser(user);
		user2 = client.createUser(user2);
	    
	    client.setupCredentials(username, password);
		
		List<Score> expectedScores = new ArrayList<Score>();
		scoreDao.insert(new Score().setExperience("100").setTime().setUserId(user.getUserId())
				.setScoreId("1").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
	    scoreDao.insert(new Score().setExperience("200").setTime().setUserId(user.getUserId())
	    		.setScoreId("2").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("300").setTime().setUserId(user.getUserId())
				.setScoreId("3").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("400").setTime().setUserId(user.getUserId())
				.setScoreId("4").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("500").setTime().setUserId(user.getUserId())
				.setScoreId("5").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("600").setTime().setUserId(user.getUserId())
				.setScoreId("6").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("700").setTime().setUserId(user.getUserId())
				.setScoreId("7").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("800").setTime().setUserId(user.getUserId())
				.setScoreId("8").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("900").setTime().setUserId(user.getUserId())
				.setScoreId("9").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1000").setTime().setUserId(user.getUserId())
				.setScoreId("10").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1100").setTime().setUserId(user.getUserId())
				.setScoreId("11").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1200").setTime().setUserId(user.getUserId())
				.setScoreId("12").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		Score scoreUser = new Score().setExperience("2000").setTime().setUserId(user2.getUserId())
				.setScoreId("13").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100");
		scoreDao.insert(scoreUser);
		
		for (Score score : expectedScores) {
			scoreDao.insert(score);
		}
		
		Collections.sort(expectedScores, new ScoreComparatorExperience());
		
		List<Score> scoresUser = client.getHighestScores(user2.getUserId(), ScoreType.EXPERIENCE);
		assertEquals("Single result should be returned", scoresUser.size(), 1);
		assertEquals("User and type should match inserted type and user", scoresUser.get(0), scoreUser);
		
		List<Score> scoresMutliple = client.getHighestScores(user.getUserId(), ScoreType.EXPERIENCE);
		assertEquals("Should return 10 results", scoresMutliple.size(), 10);
		
		int i = 0;
		for (Score score : scoresMutliple) {
			assertEquals("Retrieves records should match inserted scores.",  expectedScores.get(i), score);
			i++;
		}
	}
	
	/**
	 * Retrieve high scores from database filtered by id and Type Time using Coaster Client 
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	@Test
	public void getHighScoreByUserAndTypeTime() throws JsonProcessingException, WebApplicationException {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		CoasterClient client = new CoasterClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password);
		User user2 = new User(username + "2", "anonymous", null, "anonymous", password + "2");
		user = client.createUser(user);
		user2 = client.createUser(user2);
	    
	    client.setupCredentials(username, password);
		
		List<Score> expectedScores = new ArrayList<Score>();
		scoreDao.insert(new Score().setExperience("100").setTime().setUserId(user.getUserId())
				.setScoreId("1").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("62"));
	    scoreDao.insert(new Score().setExperience("200").setTime().setUserId(user.getUserId())
	    		.setScoreId("2").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("61"));
		expectedScores.add(new Score().setExperience("300").setTime().setUserId(user.getUserId())
				.setScoreId("3").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("60"));
		expectedScores.add(new Score().setExperience("400").setTime().setUserId(user.getUserId())
				.setScoreId("4").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("59"));
		expectedScores.add(new Score().setExperience("500").setTime().setUserId(user.getUserId())
				.setScoreId("5").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("58"));
		expectedScores.add(new Score().setExperience("600").setTime().setUserId(user.getUserId())
				.setScoreId("6").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("57"));
		expectedScores.add(new Score().setExperience("700").setTime().setUserId(user.getUserId())
				.setScoreId("7").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("56"));
		expectedScores.add(new Score().setExperience("800").setTime().setUserId(user.getUserId())
				.setScoreId("8").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("55"));
		expectedScores.add(new Score().setExperience("900").setTime().setUserId(user.getUserId())
				.setScoreId("9").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("54"));
		expectedScores.add(new Score().setExperience("1000").setTime().setUserId(user.getUserId())
				.setScoreId("10").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("53"));
		expectedScores.add(new Score().setExperience("1100").setTime().setUserId(user.getUserId())
				.setScoreId("11").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("52"));
		expectedScores.add(new Score().setExperience("1200").setTime().setUserId(user.getUserId())
				.setScoreId("12").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("51"));
		Score scoreUser = new Score().setExperience("2000").setTime().setUserId(user2.getUserId())
				.setScoreId("13").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("10");
		scoreDao.insert(scoreUser);
		
		for (Score score : expectedScores) {
			scoreDao.insert(score);
		}
		
		Collections.sort(expectedScores, new ScoreComparatorTime());
		
		List<Score> scoresUser = client.getHighestScores(user2.getUserId(), ScoreType.TIME);
		assertEquals("Single result should be returned", scoresUser.size(), 1);
		assertEquals("User and type should match inserted type and user", scoresUser.get(0), scoreUser);
		
		List<Score> scoresMutliple = client.getHighestScores(user.getUserId(), ScoreType.TIME);
		assertEquals("Should return 10 results", scoresMutliple.size(), 10);
		
		int i = 0;
		for (Score score : scoresMutliple) {
			assertEquals("Retrieves records should match inserted scores.",  expectedScores.get(i), score);
			i++;
		}
	}
	
	/**
	 * Retrieve high scores from database filtered by id and Type Kills using Coaster Client 
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	@Test
	public void getHighScoreByUserAndTypeKills() throws JsonProcessingException, WebApplicationException {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		CoasterClient client = new CoasterClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password);
		User user2 = new User(username + "2", "anonymous", null, "anonymous", password + "2");
		user = client.createUser(user);
		user2 = client.createUser(user2);
	    
	    client.setupCredentials(username, password);
		
		List<Score> expectedScores = new ArrayList<Score>();
		scoreDao.insert(new Score().setExperience("100").setTime().setUserId(user.getUserId())
				.setScoreId("1").setBossKills("100").setKills("2").setNetWorth("5").setPlayTime("62"));
	    scoreDao.insert(new Score().setExperience("200").setTime().setUserId(user.getUserId())
	    		.setScoreId("2").setBossKills("100").setKills("1").setNetWorth("5").setPlayTime("61"));
		expectedScores.add(new Score().setExperience("300").setTime().setUserId(user.getUserId())
				.setScoreId("3").setBossKills("100").setKills("8").setNetWorth("5").setPlayTime("60"));
		expectedScores.add(new Score().setExperience("400").setTime().setUserId(user.getUserId())
				.setScoreId("4").setBossKills("100").setKills("7").setNetWorth("5").setPlayTime("59"));
		expectedScores.add(new Score().setExperience("500").setTime().setUserId(user.getUserId())
				.setScoreId("5").setBossKills("100").setKills("6").setNetWorth("5").setPlayTime("58"));
		expectedScores.add(new Score().setExperience("600").setTime().setUserId(user.getUserId())
				.setScoreId("6").setBossKills("100").setKills("5").setNetWorth("5").setPlayTime("57"));
		expectedScores.add(new Score().setExperience("700").setTime().setUserId(user.getUserId())
				.setScoreId("7").setBossKills("100").setKills("4").setNetWorth("5").setPlayTime("56"));
		expectedScores.add(new Score().setExperience("800").setTime().setUserId(user.getUserId())
				.setScoreId("8").setBossKills("100").setKills("3").setNetWorth("5").setPlayTime("55"));
		expectedScores.add(new Score().setExperience("900").setTime().setUserId(user.getUserId())
				.setScoreId("9").setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("54"));
		expectedScores.add(new Score().setExperience("1000").setTime().setUserId(user.getUserId())
				.setScoreId("10").setBossKills("100").setKills("25").setNetWorth("5").setPlayTime("53"));
		expectedScores.add(new Score().setExperience("1100").setTime().setUserId(user.getUserId())
				.setScoreId("11").setBossKills("100").setKills("15").setNetWorth("5").setPlayTime("52"));
		expectedScores.add(new Score().setExperience("1200").setTime().setUserId(user.getUserId())
				.setScoreId("12").setBossKills("100").setKills("20").setNetWorth("5").setPlayTime("51"));
		Score scoreUser = new Score().setExperience("2000").setTime().setUserId(user2.getUserId())
				.setScoreId("13").setBossKills("100").setKills("100").setNetWorth("5").setPlayTime("100");
		scoreDao.insert(scoreUser);
		
		for (Score score : expectedScores) {
			scoreDao.insert(score);
		}
		
		Collections.sort(expectedScores, new ScoreComparatorKills());
		
		List<Score> scoresUser = client.getHighestScores(user2.getUserId(), ScoreType.KILLS);
		assertEquals("Single result should be returned", scoresUser.size(), 1);
		assertEquals("User and type should match inserted type and user", scoresUser.get(0), scoreUser);
		
		List<Score> scoresMutliple = client.getHighestScores(user.getUserId(), ScoreType.KILLS);
		assertEquals("Should return 10 results", scoresMutliple.size(), 10);
		
		int i = 0;
		for (Score score : scoresMutliple) {
			assertEquals("Retrieves records should match inserted scores.",  expectedScores.get(i), score);
			i++;
		}
	}
	
	/**
	 * Retrieve high scores from database filtered by id and Type BossKills using Coaster Client 
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	@Test
	public void getHighScoreByUserAndTypeBossKills() throws JsonProcessingException, WebApplicationException {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		CoasterClient client = new CoasterClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password);
		User user2 = new User(username + "2", "anonymous", null, "anonymous", password + "2");
		user = client.createUser(user);
		user2 = client.createUser(user2);
	    
	    client.setupCredentials(username, password);
		
		List<Score> expectedScores = new ArrayList<Score>();
		scoreDao.insert(new Score().setExperience("5").setTime().setUserId(user.getUserId())
				.setScoreId("1").setBossKills("4").setKills("10").setNetWorth("5").setPlayTime("62"));
	    scoreDao.insert(new Score().setExperience("200").setTime().setUserId(user.getUserId())
	    		.setScoreId("2").setBossKills("10").setKills("9").setNetWorth("5").setPlayTime("61"));
		expectedScores.add(new Score().setExperience("300").setTime().setUserId(user.getUserId())
				.setScoreId("3").setBossKills("100").setKills("8").setNetWorth("5").setPlayTime("60"));
		expectedScores.add(new Score().setExperience("400").setTime().setUserId(user.getUserId())
				.setScoreId("4").setBossKills("10").setKills("7").setNetWorth("5").setPlayTime("59"));
		expectedScores.add(new Score().setExperience("500").setTime().setUserId(user.getUserId())
				.setScoreId("5").setBossKills("20").setKills("6").setNetWorth("5").setPlayTime("58"));
		expectedScores.add(new Score().setExperience("600").setTime().setUserId(user.getUserId())
				.setScoreId("6").setBossKills("30").setKills("5").setNetWorth("5").setPlayTime("57"));
		expectedScores.add(new Score().setExperience("700").setTime().setUserId(user.getUserId())
				.setScoreId("7").setBossKills("40").setKills("4").setNetWorth("5").setPlayTime("56"));
		expectedScores.add(new Score().setExperience("800").setTime().setUserId(user.getUserId())
				.setScoreId("8").setBossKills("50").setKills("3").setNetWorth("5").setPlayTime("55"));
		expectedScores.add(new Score().setExperience("900").setTime().setUserId(user.getUserId())
				.setScoreId("9").setBossKills("60").setKills("10").setNetWorth("5").setPlayTime("54"));
		expectedScores.add(new Score().setExperience("1000").setTime().setUserId(user.getUserId())
				.setScoreId("10").setBossKills("70").setKills("25").setNetWorth("5").setPlayTime("53"));
		expectedScores.add(new Score().setExperience("1100").setTime().setUserId(user.getUserId())
				.setScoreId("11").setBossKills("80").setKills("15").setNetWorth("5").setPlayTime("52"));
		expectedScores.add(new Score().setExperience("1200").setTime().setUserId(user.getUserId())
				.setScoreId("12").setBossKills("90").setKills("20").setNetWorth("5").setPlayTime("51"));
		Score scoreUser = new Score().setExperience("2000").setTime().setUserId(user2.getUserId())
				.setScoreId("13").setBossKills("200").setKills("100").setNetWorth("5").setPlayTime("100");
		scoreDao.insert(scoreUser);
		
		for (Score score : expectedScores) {
			scoreDao.insert(score);
		}
		
		Collections.sort(expectedScores, new ScoreComparatorBossKills());
		
		List<Score> scoresUser = client.getHighestScores(user2.getUserId(), ScoreType.BOSSES);
		assertEquals("Single result should be returned", scoresUser.size(), 1);
		assertEquals("User and type should match inserted type and user", scoresUser.get(0), scoreUser);
		
		List<Score> scoresMutliple = client.getHighestScores(user.getUserId(), ScoreType.BOSSES);
		assertEquals("Should return 10 results", scoresMutliple.size(), 10);
		
		int i = 0;
		for (Score score : scoresMutliple) {
			assertEquals("Retrieves records should match inserted scores.",  expectedScores.get(i), score);
			i++;
		}
	}
	
	/**
	 * Retrieve high scores from database filtered by id and Type Worth using Coaster Client 
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	@Test
	public void getHighScoreByUserAndTypeWorth() throws JsonProcessingException, WebApplicationException {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		CoasterClient client = new CoasterClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password);
		User user2 = new User(username + "2", "anonymous", null, "anonymous", password + "2");
		user = client.createUser(user);
		user2 = client.createUser(user2);
	    
	    client.setupCredentials(username, password);
		
		List<Score> expectedScores = new ArrayList<Score>();
		scoreDao.insert(new Score().setExperience("100").setTime().setUserId(user.getUserId())
				.setScoreId("1").setBossKills("100").setKills("10").setNetWorth("6").setPlayTime("62"));
	    scoreDao.insert(new Score().setExperience("200").setTime().setUserId(user.getUserId())
	    		.setScoreId("2").setBossKills("10").setKills("9").setNetWorth("5").setPlayTime("61"));
		expectedScores.add(new Score().setExperience("300").setTime().setUserId(user.getUserId())
				.setScoreId("3").setBossKills("5").setKills("8").setNetWorth("100").setPlayTime("60"));
		expectedScores.add(new Score().setExperience("400").setTime().setUserId(user.getUserId())
				.setScoreId("4").setBossKills("10").setKills("7").setNetWorth("90").setPlayTime("59"));
		expectedScores.add(new Score().setExperience("500").setTime().setUserId(user.getUserId())
				.setScoreId("5").setBossKills("20").setKills("6").setNetWorth("80").setPlayTime("58"));
		expectedScores.add(new Score().setExperience("600").setTime().setUserId(user.getUserId())
				.setScoreId("6").setBossKills("30").setKills("5").setNetWorth("70").setPlayTime("57"));
		expectedScores.add(new Score().setExperience("700").setTime().setUserId(user.getUserId())
				.setScoreId("7").setBossKills("40").setKills("4").setNetWorth("60").setPlayTime("56"));
		expectedScores.add(new Score().setExperience("800").setTime().setUserId(user.getUserId())
				.setScoreId("8").setBossKills("50").setKills("3").setNetWorth("50").setPlayTime("55"));
		expectedScores.add(new Score().setExperience("900").setTime().setUserId(user.getUserId())
				.setScoreId("9").setBossKills("60").setKills("10").setNetWorth("40").setPlayTime("54"));
		expectedScores.add(new Score().setExperience("1000").setTime().setUserId(user.getUserId())
				.setScoreId("10").setBossKills("70").setKills("25").setNetWorth("30").setPlayTime("53"));
		expectedScores.add(new Score().setExperience("1100").setTime().setUserId(user.getUserId())
				.setScoreId("11").setBossKills("80").setKills("15").setNetWorth("20").setPlayTime("52"));
		expectedScores.add(new Score().setExperience("1200").setTime().setUserId(user.getUserId())
				.setScoreId("12").setBossKills("90").setKills("20").setNetWorth("10").setPlayTime("51"));
		Score scoreUser = new Score().setExperience("2000").setTime().setUserId(user2.getUserId())
				.setScoreId("13").setBossKills("200").setKills("100").setNetWorth("2000").setPlayTime("100");
		scoreDao.insert(scoreUser);
		
		for (Score score : expectedScores) {
			scoreDao.insert(score);
		}
		
		Collections.sort(expectedScores, new ScoreComparatorWorth());
		
		List<Score> scoresUser = client.getHighestScores(user2.getUserId(), ScoreType.WORTH);
		assertEquals("Single result should be returned", scoresUser.size(), 1);
		assertEquals("User and type should match inserted type and user", scoresUser.get(0), scoreUser);
		
		List<Score> scoresMutliple = client.getHighestScores(user.getUserId(), ScoreType.WORTH);
		assertEquals("Should return 10 results", scoresMutliple.size(), 10);
		
		int i = 0;
		for (Score score : scoresMutliple) {
			assertEquals("Retrieves records should match inserted scores.",  expectedScores.get(i), score);
			i++;
		}
	}

	/**
	 * Retrieve high scores from database filtered by Type Experience using CoasterClient
	 * @throws WebApplicationException 
	 * @throws JsonProcessingException 
	 */
	@Test 
	public void getHighScoresByType() throws JsonProcessingException, WebApplicationException {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		CoasterClient client = new CoasterClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password);
		user = client.createUser(user);
	    
	    client.setupCredentials(username, password);
		
		List<Score> expectedScores = new ArrayList<Score>();
		scoreDao.insert(new Score().setExperience("100").setTime().setUserId(user.getUserId()).setScoreId("1")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
	    scoreDao.insert(new Score().setExperience("200").setTime().setUserId(user.getUserId()).setScoreId("2")
	    		.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("300").setTime().setUserId(user.getUserId()).setScoreId("3")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("400").setTime().setUserId(user.getUserId()).setScoreId("4")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("500").setTime().setUserId(user.getUserId()).setScoreId("5")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("600").setTime().setUserId(user.getUserId()).setScoreId("6")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("700").setTime().setUserId(user.getUserId()).setScoreId("7")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("800").setTime().setUserId(user.getUserId()).setScoreId("8")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("900").setTime().setUserId(user.getUserId()).setScoreId("9")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1000").setTime().setUserId(user.getUserId()).setScoreId("10")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1100").setTime().setUserId(user.getUserId()).setScoreId("11")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1200").setTime().setUserId(user.getUserId()).setScoreId("12")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		
		for (Score score : expectedScores) {
			scoreDao.insert(score);
		}
		
		
		// EXPERIENCE 
		Collections.sort(expectedScores, new ScoreComparatorExperience());
		
		List<Score> scoresMutliple = client.getHighestScores(ScoreType.EXPERIENCE);
		assertEquals("Should return 10 results", scoresMutliple.size(), 10);
		
		int i = 0;
		for (Score score : scoresMutliple) {
			assertEquals("Retrieves records should match inserted scores.",  expectedScores.get(i), score);
			i++;
		}
		
		// TIME 
		Collections.sort(expectedScores, new ScoreComparatorTime());
		
		List<Score> scoresMutlipleTime = client.getHighestScores(ScoreType.TIME);
		assertEquals("Should return 10 results", scoresMutlipleTime.size(), 10);
		
		int j = 0;
		for (Score score : scoresMutlipleTime) {
			assertEquals("Retrieves records should match inserted scores.",  expectedScores.get(j), score);
			j++;
		}
		
		// KILLS
		Collections.sort(expectedScores, new ScoreComparatorKills());
		
		List<Score> scoresMutlipleKills = client.getHighestScores(ScoreType.KILLS);
		assertEquals("Should return 10 results", scoresMutlipleKills.size(), 10);
		
		int k = 0;
		for (Score score : scoresMutlipleKills) {
			assertEquals("Retrieves records should match inserted scores.",  expectedScores.get(k), score);
			k++;
		}
		
		// BOSSES
		Collections.sort(expectedScores, new ScoreComparatorBossKills());
		
		List<Score> scoresMutlipleBosses = client.getHighestScores(ScoreType.KILLS);
		assertEquals("Should return 10 results", scoresMutlipleBosses.size(), 10);
		
		int l = 0;
		for (Score score : scoresMutlipleBosses) {
			assertEquals("Retrieves records should match inserted scores.",  expectedScores.get(l), score);
			l++;
		}
		
		// WORTH
		Collections.sort(expectedScores, new ScoreComparatorWorth());
		
		List<Score> scoresMutlipleWorth = client.getHighestScores(ScoreType.KILLS);
		assertEquals("Should return 10 results", scoresMutlipleWorth.size(), 10);
		
		int m = 0;
		for (Score score : scoresMutlipleWorth) {
			assertEquals("Retrieves records should match inserted scores.",  expectedScores.get(m), score);
			m++;
		}
		
	}
	
	/**
	 * Get Highest score of all time using CoasterClient
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	@Test
	public void getHighScoreByType() throws JsonProcessingException, WebApplicationException {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		CoasterClient client = new CoasterClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password);
		User user2 = new User(username + "2", "anonymous", null, "anonymous", password + "2");
		user = client.createUser(user);
		user2 = client.createUser(user2);
	    
	    client.setupCredentials(username, password);
		
		List<Score> expectedScores = new ArrayList<Score>();
		scoreDao.insert(new Score().setExperience("100").setTime().setUserId(user.getUserId()).setScoreId("1")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
	    scoreDao.insert(new Score().setExperience("200").setTime().setUserId(user.getUserId()).setScoreId("2")
	    		.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("300").setTime().setUserId(user.getUserId()).setScoreId("3")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("400").setTime().setUserId(user.getUserId()).setScoreId("4")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("500").setTime().setUserId(user.getUserId()).setScoreId("5")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("600").setTime().setUserId(user.getUserId()).setScoreId("6")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("700").setTime().setUserId(user.getUserId()).setScoreId("7")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("800").setTime().setUserId(user.getUserId()).setScoreId("8")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("900").setTime().setUserId(user.getUserId()).setScoreId("9")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1000").setTime().setUserId(user.getUserId()).setScoreId("10")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1100").setTime().setUserId(user.getUserId()).setScoreId("11")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		Score insertedScore = new Score().setExperience("1200").setTime().setUserId(user2.getUserId()).setScoreId("12")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100");
		expectedScores.add(insertedScore);
		
		for (Score score : expectedScores) {
			scoreDao.insert(score);
		}
		
		// EXPERIENCE 
		Collections.sort(expectedScores, new ScoreComparatorExperience());
		
		Score score = client.getHighestScore(ScoreType.EXPERIENCE);
		assertEquals("Retrieved score should equal highest inserted score.", insertedScore, score);
		
		Score newScore = new Score().setExperience("2000").setTime().setUserId(user.getUserId()).setScoreId("13");
		scoreDao.insert(newScore);
		
		Score retrievedScore = client.getHighestScore(ScoreType.EXPERIENCE);
		assertEquals("Retrieved score should equal highest inserted score.", newScore, retrievedScore);
	}
	
	/**
	 * Get Highest score of all time using CoasterClient and given UserId
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	@Test
	public void getHighScoreByIdAndType() throws JsonProcessingException, WebApplicationException {
		String username = "testUsername";
		String password = "testPassword";
		
		DBI dbi = TEST_ENVIRONMENT.getDbi();
		CoasterClient client = new CoasterClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
		ScoreDao scoreDao = dbi.onDemand(ScoreDao.class);
		
		User user = new User(username, "anonymous", null, "anonymous", password);
		User user2 = new User(username + "2", "anonymous", null, "anonymous", password + "2");
		user = client.createUser(user);
		user2 = client.createUser(user2);
	    
	    client.setupCredentials(username, password);
		
		List<Score> expectedScores = new ArrayList<Score>();
		scoreDao.insert(new Score().setExperience("100").setTime().setUserId(user.getUserId()).setScoreId("1")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
	    scoreDao.insert(new Score().setExperience("200").setTime().setUserId(user.getUserId()).setScoreId("2")
	    		.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("300").setTime().setUserId(user.getUserId()).setScoreId("3")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("400").setTime().setUserId(user.getUserId()).setScoreId("4")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("500").setTime().setUserId(user.getUserId()).setScoreId("5")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("600").setTime().setUserId(user.getUserId()).setScoreId("6")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("700").setTime().setUserId(user.getUserId()).setScoreId("7")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("800").setTime().setUserId(user.getUserId()).setScoreId("8")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("900").setTime().setUserId(user.getUserId()).setScoreId("9")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		expectedScores.add(new Score().setExperience("1000").setTime().setUserId(user.getUserId()).setScoreId("10")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100"));
		Score insertedScoreUser = new Score().setExperience("1100").setTime().setUserId(user.getUserId()).setScoreId("11")
				.setBossKills("100").setKills("10").setNetWorth("5").setPlayTime("100");
		expectedScores.add(insertedScoreUser);
		Score insertedScore = new Score().setExperience("1200").setTime().setUserId(user2.getUserId()).setScoreId("12");
		expectedScores.add(insertedScore);
		
		for (Score score : expectedScores) {
			scoreDao.insert(score);
		}
		
		Collections.sort(expectedScores, new ScoreComparatorExperience());
		
		Score score = client.getHighestScore(user2.getUserId(), ScoreType.EXPERIENCE);
		assertEquals("Retrieved score should equal highest inserted score.", insertedScore, score);
		
		Score retrievedScore = client.getHighestScore(user.getUserId(), ScoreType.EXPERIENCE);
		assertEquals("Retrieved score should equal highest inserted score.", insertedScoreUser, retrievedScore);
	}

	/**
	 * Score comparitor for Score Lists to be sorted by Time. 
	 * @author Kellie Lutze
	 *
	 */
	static class ScoreComparatorTime implements Comparator<Score> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Score o1, Score o2) {
			return Integer.parseInt(o1.getPlayTime()) - Integer.parseInt(o2.getPlayTime());
		}
	}
	
	/**
	 * Score comparitor for Score Lists to be sorted by Kills. 
	 * @author Kellie Lutze
	 *
	 */
	static class ScoreComparatorKills implements Comparator<Score> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Score o1, Score o2) {
			return Integer.parseInt(o2.getKills()) - Integer.parseInt(o1.getKills());
		}
	}
	
	/**
	 * Score comparitor for Score Lists to be sorted by BossKills. 
	 * @author Kellie Lutze
	 *
	 */
	static class ScoreComparatorBossKills implements Comparator<Score> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Score o1, Score o2) {
			return Integer.parseInt(o2.getBossKills()) - Integer.parseInt(o1.getBossKills());
		}
	}
	
	/**
	 * Score comparitor for Score Lists to be sorted by Worth. 
	 * @author Kellie Lutze
	 *
	 */
	static class ScoreComparatorWorth implements Comparator<Score> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Score o1, Score o2) {
			return Integer.parseInt(o2.getNetWorth()) - Integer.parseInt(o1.getNetWorth());
		}
	}
	
	
	/**
	 * Score comparitor for Score Lists to be sorted by Experience. 
	 * @author Kellie Lutze
	 *
	 */
	static class ScoreComparatorExperience implements Comparator<Score> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Score o1, Score o2) {
			return Integer.parseInt(o2.getExperience()) - Integer.parseInt(o1.getExperience());
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