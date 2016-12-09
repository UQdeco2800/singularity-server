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
import org.junit.Test;

import uq.deco2800.singularity.common.representations.coaster.Score;

/**
 * @author Kellie Lutze
 */
public class ScoreTest {
	
	@Test
	public void createScore() {
		String scoreId = "1";
		String experience = "100";
		String playTime = "5";
		String kills = "250";
		String bossKills = "100";
		String netWorth = "25";
		
		// Format it using different technique 
		String timeStamp = Long.toString(System.currentTimeMillis());
		String userId = "1";
		
		Score score = new Score(scoreId, experience, userId, playTime, kills, bossKills, netWorth).setTime(timeStamp);
		
		assertEquals("Score id must match submitted score id", scoreId, score.getScoreId());
		assertEquals("Score experience must match submitted score experience", experience, score.getExperience());
		assertEquals("Timestamp string must match submitted timestamp", timeStamp, score.getTime());
		assertEquals("User Id must match submitted user id", userId, score.getUserId());
		assertEquals("Playtime must match submitted playtime", playTime, score.getPlayTime());
		assertEquals("Kills must match submitted kills", kills, score.getKills());
		assertEquals("Bosskills must match submitted bosskills",  bossKills, score.getBossKills());
		assertEquals("Networth must match submitted network", netWorth, score.getNetWorth());
	}
	
	@Test 
	public void setExperience() {
		Score score = new Score().setScoreId("1").setExperience("100").setTime().setUserId("1");
		
		assertEquals("Experience must match set experience", "100", score.getExperience());
		
		// Update experience 
		score.setExperience(100);
		assertEquals("Experience must match set experience", "100", score.getExperience());
	}
	
	@Test 
	public void hashcode() {
		
		Score score2 = new Score()
				.setScoreId("1")
				.setExperience("100")
				.setTime(timestamp)
				.setUserId("1")
				.setBossKills("25")
				.setKills("100")
				.setNetWorth("10")
				.setPlayTime("5");
		
		Score scoreDifferent = new Score()
				.setScoreId("2")
				.setExperience("900")
				.setTime()
				.setUserId("12")
				.setBossKills("0")
				.setKills("50")
				.setNetWorth("15")
				.setPlayTime("10");
		
		assertEquals("Hashcode for both scores needs to match", score.hashCode(), score2.hashCode());
		assertNotEquals("Hashcode for different scores cannot be the same", score.hashCode(), scoreDifferent.hashCode());
	}
	
	@Test 
	public void equals() {
		
		// Time stamp different based on first time stamp time to ensure difference
		long timestamp = System.currentTimeMillis();
		long timestampDifferent = timestamp - 100;
		
		Score score = new Score()
				.setScoreId("1")
				.setExperience("100")
				.setTime(timestamp)
				.setUserId("1")
				.setUserId("1")
				.setBossKills("25")
				.setKills("100")
				.setNetWorth("10")
				.setPlayTime("5");
		
		Score score2 = new Score()
				.setScoreId("1")
				.setExperience("100")
				.setTime(timestamp)
				.setUserId("1")
				.setBossKills("25")
				.setKills("100")
				.setNetWorth("10")
				.setPlayTime("5");
		
		Score scoreDifferent = new Score()
				.setScoreId("2")
				.setExperience("900")
				.setTime(timestampDifferent)
				.setUserId("12")
				.setBossKills("5")
				.setKills("50")
				.setNetWorth("15")
				.setPlayTime("10");
		
		assertTrue("The same score instance should be equal to itself", score.equals(score));
		assertFalse("A null score should be false", score.equals(null));
		assertFalse("A score should not be equal to a different object", score.equals(new StringBuilder()));
		
		Score scoreNullBoss = new Score()
				.setScoreId(score.getScoreId())
				.setExperience(score.getExperience())
				.setTime(score.getTime())
				.setUserId(score.getUserId())
				.setKills(score.getKills())
				.setNetWorth(score.getNetWorth())
				.setPlayTime(score.getPlayTime());
		scoreNullBoss.setBossKills(null);
		assertFalse("A score will not be equal if it has null boss kills but the other doesn't", scoreNullBoss.equals(score));
		
		Score scoreNullExperience = new Score()
				.setScoreId(score.getScoreId())
				.setTime(score.getTime())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setNetWorth(score.getNetWorth())
				.setPlayTime(score.getPlayTime());
		scoreNullBoss.setExperience(null);
		assertFalse("A score will null experience will not be equal to a score will non null experience", scoreNullExperience.equals(score));
		
		Score scoreNotEqualExperience = new Score()
				.setScoreId(score.getScoreId())
				.setTime(score.getTime())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setNetWorth(score.getNetWorth())
				.setPlayTime(score.getPlayTime());
		scoreNotEqualExperience.setExperience(Integer.parseInt(score.getExperience()) + 10); 
		assertFalse("If non null scores are not equal, a score cannot be equal", score.equals(scoreNotEqualExperience));
		
		Score scoreNullKills = new Score()
				.setScoreId(score.getScoreId())
				.setExperience(score.getExperience())
				.setTime(score.getTime())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setNetWorth(score.getNetWorth())
				.setPlayTime(score.getPlayTime());
		scoreNullKills.setKills(null);
		assertFalse("A score with null kills cannot match a score without null kills", scoreNullKills.equals(score));
		
		Score scoreNotEqualKills = new Score()
				.setScoreId(score.getScoreId())
				.setExperience(score.getExperience())
				.setTime(score.getTime())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setNetWorth(score.getNetWorth())
				.setPlayTime(score.getPlayTime());
		scoreNotEqualKills.setKills(Integer.toString(Integer.parseInt(score.getKills()) + 10));
		assertFalse("A scores with non equal, non null kills cannot be equal", score.equals(scoreNotEqualKills));
		
		Score scoreNullNetworth = new Score()
				.setScoreId(score.getScoreId())
				.setExperience(score.getExperience())
				.setTime(score.getTime())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setPlayTime(score.getPlayTime());
		scoreNullNetworth.setNetWorth(null);
		assertFalse("A score with null net worth cannot equal one with non null net worth", scoreNullNetworth.equals(score));
		
		Score scoreNotEqualNetworth = new Score()
				.setScoreId(score.getScoreId())
				.setExperience(score.getExperience())
				.setTime(score.getTime())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setPlayTime(score.getPlayTime());
		scoreNotEqualNetworth.setNetWorth(Integer.toString(Integer.parseInt(score.getNetWorth()) + 10));
		assertFalse("Scores with non null, non equal net worth cannot be equal", score.equals(scoreNotEqualNetworth));
		
		Score scoreNullPlaytime = new Score()
				.setScoreId(score.getScoreId())
				.setExperience(score.getExperience())
				.setTime(score.getTime())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setNetWorth(score.getNetWorth());
		scoreNullPlaytime.setPlayTime(null);
		assertFalse("A score with null play time cannot equal one with non null playtime", scoreNullPlaytime.equals(score));
		
		Score scoreNotEqualPlaytime = new Score()
				.setScoreId(score.getScoreId())
				.setExperience(score.getExperience())
				.setTime(score.getTime())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setNetWorth(score.getNetWorth());
		scoreNotEqualPlaytime.setPlayTime(Integer.toString(Integer.parseInt(score.getPlayTime()) + 10));
		assertFalse("Scores with non null, non equal play time cannot be equal", score.equals(scoreNotEqualPlaytime));
		
		Score scoreNullScoreid = new Score()
				.setExperience(score.getExperience())
				.setTime(score.getTime())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setNetWorth(score.getNetWorth())
				.setPlayTime(score.getPlayTime());
		scoreNullScoreid.setScoreId(null);
		assertFalse("A score with null scoreId cannot equal one with non null scoreId", scoreNullScoreid.equals(score));
		
		Score scoreNotEqualScoreid = new Score()
				.setExperience(score.getExperience())
				.setTime(score.getTime())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setNetWorth(score.getNetWorth())
				.setPlayTime(score.getPlayTime());;
		scoreNotEqualScoreid.setScoreId(Integer.toString(Integer.parseInt(score.getScoreId()) + 10));
		assertFalse("Scores with non null, non equal scoreId cannot be equal", score.equals(scoreNotEqualScoreid));
		
		Score scoreNullTime = new Score()
				.setScoreId(score.getScoreId())
				.setExperience(score.getExperience())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setNetWorth(score.getNetWorth())
				.setPlayTime(score.getPlayTime());;
		scoreNullTime.setTime(null);
		assertFalse("A score with null time cannot equal one with non null time", scoreNullTime.equals(score));
		
		Score scoreNotEqualTime = new Score()
				.setScoreId(score.getScoreId())
				.setExperience(score.getExperience())
				.setUserId(score.getUserId())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setNetWorth(score.getNetWorth())
				.setPlayTime(score.getPlayTime());;
		scoreNotEqualTime.setTime(score.getTime() + "10");
		assertFalse("Scores with non null, non equal time cannot be equal", score.equals(scoreNotEqualTime));
		
		Score scoreNullUserid = new Score()
				.setScoreId(score.getScoreId())
				.setExperience(score.getExperience())
				.setTime(score.getTime())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setNetWorth(score.getNetWorth())
				.setPlayTime(score.getPlayTime());;
		scoreNullUserid.setUserId(null);
		assertFalse("A score with null userid cannot equal one with non null userid", scoreNullUserid.equals(score));
		
		Score scoreNotEqualUserid = new Score()
				.setScoreId(score.getScoreId())
				.setExperience(score.getExperience())
				.setTime(score.getTime())
				.setBossKills(score.getBossKills())
				.setKills(score.getKills())
				.setNetWorth(score.getNetWorth())
				.setPlayTime(score.getPlayTime());;
		scoreNotEqualUserid.setUserId(Integer.toString(Integer.parseInt(score.getUserId()) + 10));
		assertFalse("Scores with non null, non equal userid cannot be equal", score.equals(scoreNotEqualUserid));
			
		assertTrue("Two scores with the same values should be equal", score.equals(score2));
		assertFalse("Two scores with different values should not be equal", score.equals(scoreDifferent)); 
	}
	
	@Test 
	public void emptyScore() {
		Score score = new Score();
		assertNull("New Score without parameters should have null scoreId.", score.getScoreId());
		assertNull("New Score without parameters should have null experience.", score.getExperience());
		assertNull("New Score without parameters should have null timestamp.", score.getTime());
		assertNull("New Score without parameters should have null userId.", score.getUserId());
	}
	
	@Test 
	public void newScoreTimeStamp() {
		long timestamp = System.currentTimeMillis();
		Score score = new Score().setTime(timestamp);
	
		assertEquals("Set timestamp should match input timestamp", Long.toString(timestamp), score.getTime());
		
		score = new Score().setTime();
		assertNotNull("Timestamp should be set and not null", score.getTime());
		
		// Check if time stamp as been set (Timestamp valid time is not checked as checking if timestamp is valid time causes race condition)
		score = new Score().setTime();
		assertNotNull("Timestamp should be set on initialisation.", score.getTime());
	}
	
	@Test 
	public void setNullValues() {
		Score score = new Score();
		
		score.setExperience(null);
		score.setScoreId(null);
		score.setTime(null);
		score.setUserId(null);
		score.setScoreId(null);
		score.setBossKills(null);
		score.setKills(null);
		score.setNetWorth(null);
		score.setPlayTime(null);
		
		assertNotNull("Timestamp should never be null when set  be null", score.getTime());
		assertNull("Experience can be set to null", score.getExperience());
		assertNull("UserId can be set to null", score.getUserId());
		assertNull("ScoreId can be set to null", score.getScoreId());		
		assertNull("Networth can be set to null", score.getNetWorth());		
		assertNull("Kills can be set to null", score.getKills());		
		assertNull("Boss Kills can be set to null", score.getBossKills());
		assertNull("Playtime can be set to null", score.getPlayTime());		
		
		score.setTime(0);
		assertNotNull("The time should be not be null when set to 0", score.getTime());
		
	}
}
 
