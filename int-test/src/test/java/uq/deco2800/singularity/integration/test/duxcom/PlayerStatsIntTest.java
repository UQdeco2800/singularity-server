package uq.deco2800.singularity.integration.test.duxcom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

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

import uq.deco2800.singularity.clients.duxcom.DuxcomClient;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.User;
import uq.deco2800.singularity.common.representations.duxcom.PlayerStats;
import uq.deco2800.singularity.integration.test.util.TestEnvironment;
import uq.deco2800.singularity.server.duxcom.savegame.SaveGameDao;
import uq.deco2800.singularity.server.user.UserDao;

/**
 * @author jhess-osum
 *
 */
public class PlayerStatsIntTest {
    private static final TestEnvironment TEST_ENVIRONMENT = new TestEnvironment();
    
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
     * Simple test that checks that the playerstats table is empty on start.
     */
    @Test
    public void emptyDbTest() {
        DBI dbi = TEST_ENVIRONMENT.getDbi();
        SaveGameDao saveGameDao = dbi.onDemand(SaveGameDao.class);
        int expected = 0;
        int actual = saveGameDao.getAll().size();
        Assert.assertEquals("User table should be empty to start", expected, actual);
    }
    
    /**
     * simple test that inserts 2 player stats into DB through SaveGameDao and checks 
     * that retrieved data matches inserted data
     */
    @Test
    public void insertPlayerStatsTest() {
        // test user
        String username = "Username1";
        String password = "Password1";
        
        DBI dbi = TEST_ENVIRONMENT.getDbi();
        SaveGameDao saveGameDao = dbi.onDemand(SaveGameDao.class);
        UserDao userDao = dbi.onDemand(UserDao.class);
        
        User user = new User(username, "anonymous", null, "anonymous", password);
        user.setSalt("salty");
        user.setUserId("1");
        userDao.insert(user);
        
        PlayerStats expected1 = new PlayerStats()
                .setScoreId("1")
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("100")
                .setKills("1");
        
        saveGameDao.insert(expected1);
        List<PlayerStats> playerStats = saveGameDao.getAll();
        assertTrue("There should be 1 row in db table", playerStats.size() == 1);
        
        PlayerStats expected2 = new PlayerStats()
                .setScoreId("2")
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("200")
                .setKills("1");
        
        saveGameDao.insert(expected2);
        playerStats = saveGameDao.getAll();
        assertTrue("There should be 2 rows in db table", playerStats.size() == 2);
        assertEquals("The added score should match the retrived score", expected1, saveGameDao.findByScoreId("1"));
        assertEquals("The added score should match the retrived score", expected2, saveGameDao.findByScoreId("2"));
        
    }
    
    /**
     * Finding playerStats using ScoreId
     */
    @Test
    public void findByScoreId() {
        String username = "Username1";
        String password = "Password1";
        String scoreId1 = "1";
        String scoreId2 = "2";
        
        DBI dbi = TEST_ENVIRONMENT.getDbi();
        SaveGameDao saveGameDao = dbi.onDemand(SaveGameDao.class);
        UserDao userDao = dbi.onDemand(UserDao.class);
        
        User user = new User(username, "anonymous", null, "anonymous", password);
        user.setSalt("salty");
        user.setUserId("1");
        userDao.insert(user);
        
        PlayerStats expected1 = new PlayerStats()
                .setScoreId(scoreId1)
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("100")
                .setKills("1");
        
        PlayerStats expected2 = new PlayerStats()
                .setScoreId(scoreId2)
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("200")
                .setKills("1");
        
        saveGameDao.insert(expected1);
        saveGameDao.insert(expected2);
        PlayerStats playerStats1 = saveGameDao.findByScoreId(scoreId1);
        PlayerStats playerStats2 = saveGameDao.findByScoreId(scoreId2);
        
        assertEquals("Added score Id should match retrieved score ID", scoreId1, playerStats1.getScoreId());
        assertEquals("Added score Id should match retrieved score ID", scoreId2, playerStats2.getScoreId());
   
    }
    
    /**
     * Attempt to create invalid player stats using duxcomClient
     * @throws WebApplicationException 
     * @throws JsonProcessingException 
     */
    @Test
    public void createInvalidPlayerStats() throws JsonProcessingException, WebApplicationException {
        String username = "Username1";
        String password = "Password1";
        
        DuxcomClient client = new DuxcomClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
        
        User user = new User(username, "anonymous", null, "anonymous", password);
        client.createUser(user);
        
        PlayerStats stats1 = new PlayerStats()
                .setScoreId("1")
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("-100")
                .setKills("1");
        
        PlayerStats stats2 = new PlayerStats()
                .setScoreId("2")
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("100")
                .setKills("1");
        
        client.setupCredentials(username, password);
        
        try {
            client.createPlayerStats(stats1);
            client.createPlayerStats(stats2);
        } catch (WebApplicationException | JsonProcessingException e) {
            if (((WebApplicationException) e).getResponse().getStatus() == 400) {
                return;
            }
            fail("Failed to create score with Exception " + e);
        }
        
    }
    
    /**
     * Attempt to create score with 0 valued score, experience and level
     * @throws WebApplicationException 
     * @throws JsonProcessingException 
     */
    @Test
    public void CreateInvalidZeroPlayerStats() throws JsonProcessingException, WebApplicationException {
        String username = "Username1";
        String password = "Password1";
        
        DuxcomClient client = new DuxcomClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
        
        User user = new User(username, "anonymous", null, "anonymous", password);
        client.createUser(user);
        
        PlayerStats stats1 = new PlayerStats()
                .setScoreId("1")
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("0")
                .setKills("1");
        
        PlayerStats stats2 = new PlayerStats()
                .setScoreId("1")
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("100")
                .setKills("1");
        
        PlayerStats stats3 = new PlayerStats()
                .setScoreId("1")
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("100")
                .setKills("1");
        
        client.setupCredentials(username, password);
        
        try {
            client.createPlayerStats(stats1);
            client.createPlayerStats(stats2);
            client.createPlayerStats(stats3);
        } catch (WebApplicationException | JsonProcessingException e) {
            if (((WebApplicationException) e).getResponse().getStatus() == 400) {
                return;
            }
            fail("Failed to create score with Exception " + e);
        }
        
    }
    
    @Test
    public void TestHighscoresByTypeClient() throws JsonProcessingException, WebApplicationException {
        String username = "Username1";
        String password = "Password1";
        
        DuxcomClient client = new DuxcomClient(ServerConstants.LOCAL_HOST, getRestApplicationPort());
        
        User user = new User(username, "anonymous", null, "anonymous", password);
        client.createUser(user);
        
        PlayerStats stats1 = new PlayerStats()
                .setScoreId("1")
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("100")
                .setKills("1");
        
        PlayerStats stats2 = new PlayerStats()
                .setScoreId("2")
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("300")
                .setKills("3");
        
        PlayerStats stats3 = new PlayerStats()
                .setScoreId("3")
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("200")
                .setKills("2");
        
        PlayerStats stats4 = new PlayerStats()
                .setScoreId("4")
                .setUserId(user.getUserId())
                .setNewTimestamp()
                .setScore("400")
                .setKills("4");
        
        client.setupCredentials(username, password);
        
        client.createPlayerStats(stats1);
        client.createPlayerStats(stats2);
        client.createPlayerStats(stats3);
        client.createPlayerStats(stats4);
        
        List<PlayerStats> actualPlayerStats = new ArrayList<PlayerStats>();
        actualPlayerStats.add(stats4);
        actualPlayerStats.add(stats2);
        actualPlayerStats.add(stats3);
        actualPlayerStats.add(stats1);
        
        List<PlayerStats> retrievedPlayerStats = client.getHighscoresByType("SCORE");
        
        assertEquals("retrieved list shoud be the same as actual player stats", actualPlayerStats, retrievedPlayerStats);
        
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
