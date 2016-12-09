package uq.deco2800.singularity.common.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;

import org.junit.Test;

import uq.deco2800.singularity.common.representations.duxcom.PlayerStats;

/**
 * @author jhess-osum
 *
 */
public class SaveGameTest {
    
    /**
     * Test to check null player stats
     */
    @Test
    public void emptyPlayerStats() {
        PlayerStats playerStats = new PlayerStats();
        assertNull("New Score without parameters should have null scoreId.", playerStats.getScoreId());
        assertNull("New Score without parameters should have null userId.", playerStats.getUserId());
        assertNull("New Score without parameters should have null timestamp.", playerStats.getTimestamp());
        assertNull("New Score without parameters should have null type.", playerStats.getKills());
        assertNull("New Score without parameters should have null score.", playerStats.getScore());
        
    }
    
    
    /**
     * Test to check the timestamp is saved correctly
     */
    @Test
    public void timestampTesting() {
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        PlayerStats playerStats = new PlayerStats();
        playerStats.setTimestamp(timestamp);
        
        //check timetamp is saved correctly
        assertEquals("Timestamps should match up!", timestamp.toString(), 
                playerStats.getTimestamp().toString());
        
        //check for bad timestamp
        playerStats = new PlayerStats();
        playerStats.setTimestamp(123456789);
        assertNull("Timestamp should have not been set. Should be null", playerStats.getTimestamp());
        
    }
    
    /**
     * 
     */
    @Test
    public void setNullValues(){
        PlayerStats playerStats = new PlayerStats();
        
        playerStats.setScoreId(null);
        playerStats.setUserId(null);
        playerStats.setTimestamp(null);
        playerStats.setKills(null);
        playerStats.setScore(null);
        
        assertNotNull("timestamp should never be null when set to null");
        
        
    }
   
    
    
}
