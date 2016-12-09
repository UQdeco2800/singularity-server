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

import org.junit.Test;

import uq.deco2800.singularity.common.representations.coaster.Score;
import uq.deco2800.singularity.common.representations.pyramidscheme.CommonStatistics;

/**
 * @author 1Jamster1
 */
public class CommonStatisticsTest {
	
	@Test
	public void createStatistics() {
		String message = "must match submitted";
		
		String statID = "stats";
		String minions = "10";
		String userID = "1";
		String health = "23";
		String wins = "5";
		String losses = "2";
		String hours = "100";
		String minutes = "25";
		
		String timeStamp = (String) new Timestamp(System.currentTimeMillis()).toString();
		
		
		CommonStatistics commonStats = new CommonStatistics(statID, userID, minions, 
				minions, minions, health, health);
		
		commonStats.setLastLogin(timeStamp);
		commonStats.setTotalWins(wins);
		commonStats.setTotalLosses(losses);
		commonStats.setTotalHours(hours);
		commonStats.setTotalMinutes(minutes);
		
		assertEquals("Stat ID" + message + "Stat ID", statID, commonStats.getStatID());
		assertEquals("User ID" + message + "User ID", userID, commonStats.getUserID());
		assertEquals("Minions Played" + message + "Minions Played", minions, commonStats.getMinionsPlayed());
		assertEquals("Minions Lost" + message + "Minions Lost", minions, commonStats.getMinionsLost());
		assertEquals("Minions Killed" + message + "Minions Killed", minions, commonStats.getMinionsKilled());
		assertEquals("Health Lost" + message + "Health Lost", health, commonStats.getHealthLost());
		assertEquals("Health Taken" + message + "Health Taken", health, commonStats.getHealthTaken());
		assertEquals("Total Wins" + message + "Total Wins", wins, commonStats.getTotalWins());
		assertEquals("Total Losses" + message + "Total Losses", losses, commonStats.getTotalLosses());
		assertEquals("Total Hours" + message + "Total Hours", hours, commonStats.getTotalHours());
		assertEquals("Total Minutes" + message + "Total Minutes", minutes, commonStats.getTotalMinutes());
		assertEquals("Timestamp" + message + "Timestamp", timeStamp, commonStats.getLastLogin().toString());
		
		commonStats = new CommonStatistics(statID);
		assertEquals("Stat ID" + message + "Stat ID", statID, commonStats.getStatID());
	}
	
	@Test
	public void setToMethods() {
		String message = "must match submitted";
		
		String statID = "stats";
		String minions = "10";
		String userID = "1";
		String health = "23";
		String wins = "5";
		String losses = "2";
		String hours = "100";
		String minutes = "25";
		
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		
		CommonStatistics commonStats = new CommonStatistics();
		
		commonStats.setStatID(statID);
		assertEquals("Stat ID" + message + "Stat ID", statID, commonStats.getStatID());
		
		commonStats.setUserID(userID);
		assertEquals("User ID" + message + "User ID", userID, commonStats.getUserID());
		
		commonStats.setMinionsPlayed(minions);
		assertEquals("Minions Played" + message + "Minions Played", minions, commonStats.getMinionsPlayed());
		
		commonStats.setMinionsLost(minions);
		assertEquals("Minions Lost" + message + "Minions Lost", minions, commonStats.getMinionsLost());
		
		commonStats.setMinionsKilled(minions);
		assertEquals("Minions Killed" + message + "Minions Killed", minions, commonStats.getMinionsKilled());
		
		commonStats.setHealthLost(health);
		assertEquals("Health Lost" + message + "Health Lost", health, commonStats.getHealthLost());
		
		commonStats.setHealthTaken(health);
		assertEquals("Health Taken" + message + "Health Taken", health, commonStats.getHealthTaken());
		
		commonStats.setTotalWins(wins);
		assertEquals("Total Wins" + message + "Total Wins", wins, commonStats.getTotalWins());
		
		commonStats.setTotalLosses(losses);
		assertEquals("Total Losses" + message + "Total Losses", losses, commonStats.getTotalLosses());
		
		commonStats.setTotalHours(hours);
		assertEquals("Total Hours" + message + "Total Hours", hours, commonStats.getTotalHours());
		
		commonStats.setTotalMinutes(minutes);
		assertEquals("Total Minutes" + message + "Total Minutes", minutes, commonStats.getTotalMinutes());
		
		commonStats.setLastLogin(timeStamp);
		assertEquals("Timestamp" + message + "Timestamp", timeStamp, commonStats.getLastLogin());
		
		commonStats.setLastLogin(timeStamp.toString());
		assertEquals("Timestamp" + message + "Timestamp", timeStamp, commonStats.getLastLogin());
	}
	
	@Test
	public void addToMethodsString() {
		String message = "should be equal to";
		
		String statID = "stats";
		String userID = "id";
		String inital = "1";
		String added = "2";
		
		CommonStatistics commonStats = new CommonStatistics(statID, userID, inital, inital, inital, inital, inital);
		
		commonStats.setTotalWins(inital);
		commonStats.setTotalLosses(inital);
		commonStats.setTotalHours(inital);
		commonStats.setTotalMinutes(inital);
		
		commonStats.addToMinionsPlayed("1");
		assertEquals("Minions Played" + message + "Minions Played + 1", added, commonStats.getMinionsPlayed());
		
		commonStats.addToMinionsKilled("1");
		assertEquals("Minions Killed" + message + "Minions Killed + 1", added, commonStats.getMinionsKilled());
		
		commonStats.addToMinionsLost("1");
		assertEquals("Minions Lost" + message + "Minions Lost + 1", added, commonStats.getMinionsLost());
		
		commonStats.addToHealthLost("1");
		assertEquals("Health Lost" + message + "Health Lost + 1", added, commonStats.getHealthLost());
		
		commonStats.addToHealthTaken("1");
		assertEquals("Health Taken" + message + "Health Taken + 1", added, commonStats.getHealthTaken());
		
		commonStats.addToTotalWins("1");
		assertEquals("Total Wins" + message + "Total Wins + 1", added, commonStats.getTotalWins());
		
		commonStats.addToTotalLosses("1");
		assertEquals("Total Losses" + message + "Total Losses + 1", added, commonStats.getTotalLosses());
		
		commonStats.addToTotalHours("1");
		assertEquals("Total Hours" + message + "Total Hours + 1", added, commonStats.getTotalHours());
		
		commonStats.addToTotalMinutes("1");
		assertEquals("Total Minutes" + message + "Total Minutes + 1", added, commonStats.getTotalMinutes());
	}
	

	@Test
	public void addToMethodsInteger() {
		String message = "should be equal to";
		
		String statID = "stats";
		String userID = "id";
		String inital = "1";
		String added = "2";
		
		CommonStatistics commonStats = new CommonStatistics(statID, userID, inital, inital, inital, inital, inital);
		
		commonStats.setTotalWins(inital);
		commonStats.setTotalLosses(inital);
		commonStats.setTotalHours(inital);
		commonStats.setTotalMinutes(inital);
		
		commonStats.addToMinionsPlayed(1);
		assertEquals("Minions Played" + message + "Minions Played + 1", added, commonStats.getMinionsPlayed());
		
		commonStats.addToMinionsKilled(1);
		assertEquals("Minions Killed" + message + "Minions Killed + 1", added, commonStats.getMinionsKilled());
		
		commonStats.addToMinionsLost(1);
		assertEquals("Minions Lost" + message + "Minions Lost + 1", added, commonStats.getMinionsLost());
		
		commonStats.addToHealthLost(1);
		assertEquals("Health Lost" + message + "Health Lost + 1", added, commonStats.getHealthLost());
		
		commonStats.addToHealthTaken(1);
		assertEquals("Health Taken" + message + "Health Taken + 1", added, commonStats.getHealthTaken());
		
		commonStats.addToTotalWins(1);
		assertEquals("Total Wins" + message + "Total Wins + 1", added, commonStats.getTotalWins());
		
		commonStats.addToTotalLosses(1);
		assertEquals("Total Losses" + message + "Total Losses + 1", added, commonStats.getTotalLosses());
		
		commonStats.addToTotalHours(1);
		assertEquals("Total Hours" + message + "Total Hours + 1", added, commonStats.getTotalHours());
		
		commonStats.addToTotalMinutes(1);
		assertEquals("Total Minutes" + message + "Total Minutes + 1", added, commonStats.getTotalMinutes());
	}
	
	@Test
	public void toStringTest() {
		String statID = "stats";
		String userID = "id";
		String inital = "1";
		
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		
		String toStringResult = "CommonStats = (StatID: " + statID + 
				", UserID: " + userID + 
				"\nMinionsPlayed: " + inital + 
				", MinionsKilled: " + inital + 
				", MinionsLost: " + inital +
				"\nHealthLost: " + inital + 
				", HealthTaken: " + inital + 
				", LastLogin: " + timeStamp.toString() + 
				"\nTotalWins: " + inital +
				", TotalLosses: " + inital +
				", TotalHours: " + inital +
				", TotalMinutes: " + inital +
				")";
		
		CommonStatistics commonStats = new CommonStatistics(statID, userID, inital, inital, inital, inital, inital);
		
		commonStats.setTotalWins(inital);
		commonStats.setTotalLosses(inital);
		commonStats.setTotalHours(inital);
		commonStats.setTotalMinutes(inital);
		commonStats.setLastLogin(timeStamp);
		
		String result = commonStats.toString();
		assertEquals(toStringResult, result);
	}
	
	@Test
	public void hashCodeTest() {
		String statID = "stats";
		String userID = "id";
		String inital = "1";
		
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		
		CommonStatistics commonStats = new CommonStatistics(statID, userID, inital, inital, inital, inital, inital);
		
		commonStats.setTotalWins(inital);
		commonStats.setTotalLosses(inital);
		commonStats.setTotalHours(inital);
		commonStats.setTotalMinutes(inital);
		commonStats.setLastLogin(timeStamp);
		
		CommonStatistics otherStats = new CommonStatistics(statID, userID, inital, inital, inital, inital, inital);
		
		otherStats.setTotalWins(inital);
		otherStats.setTotalLosses(inital);
		otherStats.setTotalHours(inital);
		otherStats.setTotalMinutes(inital);
		otherStats.setLastLogin(timeStamp);
		
		assertEquals(otherStats.hashCode(), commonStats.hashCode());
	}
	
	@Test
	public void equalsTest() {
		String statID = "stats";
		String otherID = "lol";
		String userID = "id";
		String inital = "1";
		String other = "2";
		
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		
		CommonStatistics commonStats = new CommonStatistics(statID, userID, inital, inital, inital, inital, inital);
		
		commonStats.setTotalWins(inital);
		commonStats.setTotalLosses(inital);
		commonStats.setTotalHours(inital);
		commonStats.setTotalMinutes(inital);
		commonStats.setLastLogin(timeStamp);
		
		CommonStatistics otherStats = new CommonStatistics(statID, userID, inital, inital, inital, inital, inital);
		
		otherStats.setTotalWins(inital);
		otherStats.setTotalLosses(inital);
		otherStats.setTotalHours(inital);
		otherStats.setTotalMinutes(inital);
		otherStats.setLastLogin(timeStamp);
		
		assertTrue(commonStats.equals(otherStats));
		assertFalse(commonStats.equals(null));
		assertFalse(commonStats.equals(statID));
		
		otherStats.setStatID(otherID);
		assertFalse(commonStats.equals(otherStats));
		otherStats.setStatID(statID);
		
		otherStats.setUserID(otherID);
		assertFalse(commonStats.equals(otherStats));
		otherStats.setUserID(userID);
		
		otherStats.setMinionsPlayed(other);
		assertFalse(commonStats.equals(otherStats));
		otherStats.setMinionsPlayed(inital);
		
		otherStats.setMinionsLost(other);
		assertFalse(commonStats.equals(otherStats));
		otherStats.setMinionsLost(inital);
		
		otherStats.setMinionsKilled(other);
		assertFalse(commonStats.equals(otherStats));
		otherStats.setMinionsKilled(inital);
		
		otherStats.setHealthLost(other);
		assertFalse(commonStats.equals(otherStats));
		otherStats.setHealthLost(inital);
		
		otherStats.setHealthTaken(other);
		assertFalse(commonStats.equals(otherStats));
		otherStats.setHealthTaken(inital);
		
		otherStats.setTotalWins(other);
		assertFalse(commonStats.equals(otherStats));
		otherStats.setTotalWins(inital);
		
		otherStats.setTotalLosses(other);
		assertFalse(commonStats.equals(otherStats));
		otherStats.setTotalLosses(inital);
		
		otherStats.setTotalHours(other);
		assertFalse(commonStats.equals(otherStats));
		otherStats.setTotalHours(inital);
		
		otherStats.setTotalMinutes(other);
		assertFalse(commonStats.equals(otherStats));
	}
}
 
