package uq.deco2800.singularity.common.tests;

import org.junit.Test;

import uq.deco2800.singularity.common.representations.pyramidscheme.CommonStatistics;
import uq.deco2800.singularity.common.representations.pyramidscheme.UserStatistics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;

import org.junit.Assert;

public class UserStatisticsTest {

	@Test
	public void mostTests() {
		
		String level = "2";
		String addString = "1";
		Integer addInt = 1;
		
		
		UserStatistics userStats = new UserStatistics();
		
		Assert.assertEquals(null, userStats.getUserLevel());
		
		userStats.setUserLevel(level);
		Assert.assertEquals(level, userStats.getUserLevel());
		
		
		userStats = new UserStatistics(level);
		Assert.assertEquals(level, userStats.getUserLevel());
		
		userStats.addToUserLevel(addString);
		Assert.assertEquals("3", userStats.getUserLevel());
		
		userStats.addToUserLevel(addInt);
		Assert.assertEquals("4", userStats.getUserLevel());
	}
	
	@Test
	public void mergeTest() {
		String inital = "1";
		String finalvalue = "2";
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		UserStatistics userStats = new UserStatistics();
		
		userStats.setStatID(inital);
		userStats.setUserID(inital);
		userStats.setLastLogin(timeStamp);
		userStats.setUserLevel(inital);
		userStats.setMinionsPlayed(inital);
		userStats.setMinionsLost(inital);
		userStats.setMinionsKilled(inital);
		userStats.setHealthTaken(inital);
		userStats.setHealthLost(inital);
		userStats.setTotalWins(inital);
		userStats.setTotalLosses(inital);
		userStats.setTotalHours(inital);
		userStats.setTotalMinutes(inital);
	
		UserStatistics expect = new UserStatistics();
		
		expect.setStatID(inital);
		expect.setUserID(inital);
		expect.setLastLogin(timeStamp);
		expect.setUserLevel(finalvalue);
		expect.setMinionsPlayed(finalvalue);
		expect.setMinionsLost(finalvalue);
		expect.setMinionsKilled(finalvalue);
		expect.setHealthTaken(finalvalue);
		expect.setHealthLost(finalvalue);
		expect.setTotalWins(finalvalue);
		expect.setTotalLosses(finalvalue);
		expect.setTotalHours(finalvalue);
		expect.setTotalMinutes(finalvalue);
		
		userStats.mergeStatistics(userStats);
		
		Assert.assertEquals(userStats, expect);
	}
	
	@Test
	public void hashCodeTest() {
		String inital = "1";
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		UserStatistics userStats = new UserStatistics();
		
		userStats.setStatID(inital);
		userStats.setUserID(inital);
		userStats.setLastLogin(timeStamp);
		userStats.setUserLevel(inital);
		userStats.setMinionsPlayed(inital);
		userStats.setMinionsLost(inital);
		userStats.setMinionsKilled(inital);
		userStats.setHealthTaken(inital);
		userStats.setHealthLost(inital);
		userStats.setTotalWins(inital);
		userStats.setTotalLosses(inital);
		userStats.setTotalHours(inital);
		userStats.setTotalMinutes(inital);
		
		UserStatistics otherStats = new UserStatistics();
		
		otherStats.setStatID(inital);
		otherStats.setUserID(inital);
		otherStats.setLastLogin(timeStamp);
		otherStats.setUserLevel(inital);
		otherStats.setMinionsPlayed(inital);
		otherStats.setMinionsLost(inital);
		otherStats.setMinionsKilled(inital);
		otherStats.setHealthTaken(inital);
		otherStats.setHealthLost(inital);
		otherStats.setTotalWins(inital);
		otherStats.setTotalLosses(inital);
		otherStats.setTotalHours(inital);
		otherStats.setTotalMinutes(inital);
		
		Assert.assertEquals(userStats.hashCode(), otherStats.hashCode());
	}
	
	@Test
	public void toStringTest() {
		String inital = "1";
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		UserStatistics userStats = new UserStatistics();
		
		userStats.setStatID(inital);
		userStats.setUserID(inital);
		userStats.setLastLogin(timeStamp);
		userStats.setUserLevel(inital);
		userStats.setMinionsPlayed(inital);
		userStats.setMinionsLost(inital);
		userStats.setMinionsKilled(inital);
		userStats.setHealthTaken(inital);
		userStats.setHealthLost(inital);
		userStats.setTotalWins(inital);
		userStats.setTotalLosses(inital);
		userStats.setTotalHours(inital);
		userStats.setTotalMinutes(inital);
		
		String expect = "UserStats = (StatID: " + inital + 
				", UserID: " + inital + 
				", UserLevel: " + inital +
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
		
		Assert.assertEquals(expect, userStats.toString());
	}
	
	@Test
	public void equalsTest() {
		String statID = "stats";
		String otherID = "lol";
		String userID = "id";
		String inital = "1";
		String other = "2";
		
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		
		UserStatistics commonStats = new UserStatistics();
		
		commonStats.setStatID(statID);
		commonStats.setUserID(userID);
		commonStats.setUserLevel(inital);
		commonStats.setMinionsPlayed(inital);
		commonStats.setMinionsLost(inital);
		commonStats.setMinionsKilled(inital);
		commonStats.setHealthLost(inital);
		commonStats.setHealthTaken(inital);
		commonStats.setTotalWins(inital);
		commonStats.setTotalLosses(inital);
		commonStats.setTotalHours(inital);
		commonStats.setTotalMinutes(inital);
		commonStats.setLastLogin(timeStamp);
		
		UserStatistics otherStats = new UserStatistics();
		
		otherStats.setStatID(statID);
		otherStats.setUserID(userID);
		otherStats.setUserLevel(inital);
		otherStats.setMinionsPlayed(inital);
		otherStats.setMinionsLost(inital);
		otherStats.setMinionsKilled(inital);
		otherStats.setHealthLost(inital);
		otherStats.setHealthTaken(inital);
		otherStats.setTotalWins(inital);
		otherStats.setTotalLosses(inital);
		otherStats.setTotalHours(inital);
		otherStats.setTotalMinutes(inital);
		otherStats.setLastLogin(timeStamp);
		
		assertTrue(commonStats.equals(otherStats));
		assertFalse(commonStats.equals(null));
		assertFalse(commonStats.equals(statID));
		
		otherStats.setUserLevel(other);
		assertFalse(commonStats.equals(otherStats));
		otherStats.setUserLevel(inital);
		
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
