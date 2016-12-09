package uq.deco2800.singularity.integration.test.achievement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import uq.deco2800.singularity.common.representations.User;
import uq.deco2800.singularity.common.representations.duxcom.Achievement;
import uq.deco2800.singularity.common.representations.duxcom.AchievementType;
import uq.deco2800.singularity.common.representations.duxcom.PlayerStats;
import uq.deco2800.singularity.integration.test.util.TestEnvironment;
import uq.deco2800.singularity.server.achievement.AchievementDao;
import uq.deco2800.singularity.server.duxcom.savegame.SaveGameDao;
import uq.deco2800.singularity.server.user.UserDao;

public class AchievementIntTest {
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
        AchievementDao achievementDao = dbi.onDemand(AchievementDao.class);
        int expected = 0;
        int actual = achievementDao.getAll().size();
        Assert.assertEquals("User table should be empty to start", expected, actual);
    }
    
    /**
     * simple test that inserts 2 Achievements into DB through AchievementDao and checks 
     * that retrieved data matches inserted data
     */
    @Test
    public void insertAchievementsTest1() {
        // test user
        String username = "Username1";
        String password = "Password1";
        
        DBI dbi = TEST_ENVIRONMENT.getDbi();
        AchievementDao achievementDao = dbi.onDemand(AchievementDao.class);
        UserDao userDao = dbi.onDemand(UserDao.class);
        
        Achievement expected1 = new Achievement("1", "Food Fight", "Test", AchievementType.KILL,  500);
        
        achievementDao.insert(expected1);
        
        List<Achievement> retrievedInfo = achievementDao.getAll();
        assertTrue("There should be 1 row in db table", retrievedInfo.size() == 1); 
        
    }
    
    @Test
    public void insertAchievementsTest2() {
        // test user
        String username = "Username1";
        String password = "Password1";
        
        DBI dbi = TEST_ENVIRONMENT.getDbi();
        AchievementDao achievementDao = dbi.onDemand(AchievementDao.class);
        UserDao userDao = dbi.onDemand(UserDao.class);
        
        Achievement expected1 = new Achievement("1", "Food Fight", "Test", AchievementType.KILL,  500);
        Achievement expected2 = new Achievement("2", "Black Eye", "Test2", AchievementType.KILL,  500);
        
        achievementDao.insert(expected1);
        achievementDao.insert(expected2);
        
        Achievement retrieved1 = achievementDao.findById("1");
        Achievement retrieved2 = achievementDao.findById("2");
        
        System.out.println("**********************");
        System.out.println(retrieved1);
        System.out.println(retrieved2);
        System.out.println("**********************");
        
        List<Achievement> retrievedInfo = achievementDao.getAll();
        assertTrue("There should be 2 rows in db table", retrievedInfo.size() == 2); 
        assertEquals("Achievement sent should be the same as retirieved", expected1, retrieved1);
        assertEquals("Achievement sent should be the same as retirieved", expected2, retrieved2);
        
    }

}
