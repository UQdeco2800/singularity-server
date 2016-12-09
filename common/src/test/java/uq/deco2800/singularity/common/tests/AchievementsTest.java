/**
 * 
 */
package uq.deco2800.singularity.common.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;

import org.junit.Assert;
import org.junit.Test;

import uq.deco2800.singularity.common.representations.pyramidscheme.Achievements;
import uq.deco2800.singularity.common.representations.pyramidscheme.UserStatistics;

/**
 * @author 1Jamster1
 */
public class AchievementsTest {
	
	@Test
	public void createAchievement() {
		String message = "must match submitted";
		
		String statID = "stats";
		String userID = "1";
		String name = "hi";
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		
		Achievements achieve = new Achievements(statID, userID, name, timeStamp);
		
		assertEquals("Stat ID" + message + "Stat ID", statID,achieve.getStatID());
		assertEquals("User ID" + message + "User ID", userID, achieve.getUserID());
		assertEquals("Name" + message + "Name", name, achieve.getAchievementName());
		assertEquals("Timestamp" + message + "Timestamp", timeStamp, achieve.getTimestamp());
	}
	
	@Test
	public void setToMethods() {
		String message = "must match submitted";
		
		String statID = "stats";
		String userID = "1";
		String name = "hi";
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		
		Achievements achieve = new Achievements();
		
		achieve.setStatID(statID);
		assertEquals("Stat ID" + message + "Stat ID", statID, achieve.getStatID());
		
		achieve.setUserID(userID);
		assertEquals("User ID" + message + "User ID", userID, achieve.getUserID());
		
		achieve.setAchievementName(name);
		assertEquals("Name" + message + "Name", name, achieve.getAchievementName());
		
		achieve.setTimestamp(timeStamp);
		assertEquals("Timestamp" + message + "Timestamp", timeStamp, achieve.getTimestamp());
		
		achieve.setTimestamp(timeStamp.toString());
		assertEquals("Timestamp" + message + "Timestamp", timeStamp, achieve.getTimestamp());
	}
	
	@Test
	public void hashcodeTest() {
		String statID = "stats";
		String userID = "1";
		String name = "hi";
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		
		Achievements achieve = new Achievements(statID, userID, name, timeStamp);
		Achievements other =  new Achievements(statID, userID, name, timeStamp);
		
		assertEquals(achieve.hashCode(), other.hashCode());
		
		Assert.assertTrue(achieve.equals(other));
	}
	
	@Test
	public void toStringTest() {
		String statID = "stats";
		String userID = "1";
		String name = "hi";
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		
		Achievements achieve = new Achievements(statID, userID, name, timeStamp);
		
		String toStringResult = "Achievements (StatID: " + statID + ", UserID: " 
				+ userID + ", name: " + name + ")";

		assertEquals(toStringResult, achieve.toString());
	}
	
	@Test
	public void equalsTest() throws InterruptedException {
		String statID = "stats";
		String userID = "1";
		String name = "hi";
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		
		Thread.sleep(5);
		
		String statID2 = "sta1ts";
		String userID2 = "11";
		String name2 = "hi1";
		Timestamp timeStamp2 = new Timestamp(System.currentTimeMillis());
		
		Achievements achieve = new Achievements(statID, userID, name, timeStamp);
		Achievements other = new Achievements(statID, userID, name, timeStamp);
		
		Assert.assertTrue(achieve.equals(other));
		Assert.assertFalse(achieve.equals(statID));
		Assert.assertFalse(achieve.equals(null));
		
		other.setStatID(statID2);
		Assert.assertFalse(achieve.equals(other));
		other.setStatID(statID);
		
		other.setUserID(userID2);
		Assert.assertFalse(achieve.equals(other));
		other.setUserID(userID);
		
		other.setAchievementName(name2);
		Assert.assertFalse(achieve.equals(other));
		other.setAchievementName(name);
		
		other.setTimestamp(timeStamp2);
		Assert.assertFalse(achieve.equals(other));
	}
}
 
