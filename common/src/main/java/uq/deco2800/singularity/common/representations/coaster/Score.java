/**
 * 
 */
package uq.deco2800.singularity.common.representations.coaster;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Connor
 *
 */
//If properties are unknown when (de)serialising JSON, ignore, don't error.
@JsonIgnoreProperties(ignoreUnknown = true)
public class Score {
	
	@JsonProperty
	// Id of the current score. Generated via UUID.randomUUID().toString()
	// Should only be generated when adding new Score. 
	private String scoreId = null;
	
	@JsonProperty
	// Experience level of current score. 
	private String experience = null;
	
	@JsonProperty
	// Date score was achieved. 
	// On create new set to current unix timestamp
	private String time = null;
	
	@JsonProperty 
	// Time of play
	private String playTime = null;
	
	@JsonProperty 
	// Number of normal enemy kills
	private String kills = null;
	
	@JsonProperty 
	// Number of boss kills
	private String bossKills = null;
	
	@JsonProperty 
	// Net worth of Player 
	private String netWorth = null;
	
	@JsonProperty
	@NotEmpty
	// UserID for given score, user must exist already. 
	private String userId = null;
	
	
	/**
	 * Constructor for Score Class. Used for Jackson deserialising to object. 
	 * Will Automatically generate a new UUID for scoreId.
	 */
	public Score() {
		// Constructor for Jackson Serialising
	}
	
	
	/**
	 * Constructor to create new score object from existing score UUID 
	 * 
	 * @param scoreId
	 * 				The scores ID. String representation of UUID.
	 */
	public Score(String scoreId) {
		this.scoreId = scoreId;
	}
	
	/**
	 * Constructor to make Score class.
	 * @param scoreId
	 * 					UUID Of Score 
	 * @param experience
	 * 					Experience value of Score as a String representation of a number
	 * @param userId
	 * 					UUID of user who achieved score. 
	 * @param playTime 
	 * 					The playtime of the score in seconds. 
	 * @param kills
	 * 					The number standard of kills achieved 
	 * @param bossKills 
	 * 					The number of boss kills achieved 
	 * @param netWorth
	 * 					The networth of the player at time of score. 
	 */
	public Score(String scoreId, String experience, String userId, String playTime, String kills, String bossKills, String netWorth) {
		this.scoreId = scoreId;
		this.experience = experience;
		this.time = Long.toString(System.currentTimeMillis());
		this.userId = userId;
		this.playTime = playTime;
		this.kills = kills;
		this.bossKills = bossKills;
		this.netWorth = netWorth;
	}

	/**
	 * @return the scoreId
	 */
	public String getScoreId() {
		return scoreId;
	}


	/**
	 * @param scoreId the scoreId to set
	 */
	public Score setScoreId(String scoreId) {
		this.scoreId = scoreId;
		return this;
	}


	/**
	 * @return the experience
	 */
	public String getExperience() {
		return experience;
	}


	/**
	 * Sets Experience as a String.
	 * @param experience the experience to set as String, accepts in String or Integer form.
	 */
	public Score setExperience(Object experience) {
		if (experience instanceof String) {
			this.experience = (String) experience;
		} else if (experience instanceof Integer) {
			this.experience = Integer.toString((Integer)experience);
		}
		return this;
	}


	/**
	 * Return String representation of Unix datetime. 
	 * @return the date
	 */
	public String getTime() {
		return time;
	}

	/**
	 * Set score time, if set as null current time will be used. Otherwise, can accept time as long or as string
	 * Method overloading has been avoided due to the restrictions by the tools used with the server. If a bad input parameter, the time is set to now. 
	 * @param time the date to set
	 * @return Current Score Object 
	 */
	public Score setTime(Object time) {
		if (time instanceof Long) {
			if ((Long)time == 0) {
				return this.setTime();
			} else {
				this.time = Long.toString((Long)time);
			}
		} else if (time instanceof String) {
			this.time = (String)time;
		} else {
			this.setTime();
		}
		return this;
	}
		
		
	/**
	 * Call this method to set the time at the current unix time.
	 */
	public Score setTime() {
		this.time = Long.toString(System.currentTimeMillis() / 1000L); 
		return this;
	}


	/**
	 * Get the user id of the user associated with the score. 
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}


	/**
	 * Set the user id of the user associated with the score. 
	 * @param userId the userId to set
	 */
	public Score setUserId(String userId) {
		this.userId = userId;
		return this;
	}
	
	
	/**
	 * Get the playtime. 
	 * @return the playTime
	 */
	public String getPlayTime() {
		return playTime;
	}


	/**
	 * Set the playtime
	 * @param playTime the playTime to set
	 */
	public Score setPlayTime(String playTime) {
		this.playTime = playTime;
		return this;
	}


	/**
	 * @return the bossKills
	 */
	public String getBossKills() {
		return bossKills;
	}

	/**
	 * @param bossKills the bossKills to set
	 */
	public Score setBossKills(String bossKills) {
		this.bossKills = bossKills;
		return this;
	}
	
	/**
	 * @return the kills
	 */
	public String getKills() {
		return kills;
	}


	/**
	 * @param kills the kills to set
	 */
	public Score setKills(String kills) {
		this.kills = kills;
		return this;
	}


	/**
	 * @return the netWorth
	 */
	public String getNetWorth() {
		return netWorth;
	}


	/**
	 * @param netWorth the netWorth to set
	 */
	public Score setNetWorth(String netWorth) {
		this.netWorth = netWorth;
		return this;
	}


	@Override
	public String toString() {
		return "Score [scoreId=" 
				+ this.scoreId 
				+ ", experience=" 
				+ this.experience 
				+ ", timestamp=" 
				+ this.time 
				+ ", userId=" 
				+ this.userId
				+ ", netWorth="
				+ this.getNetWorth()
				+ ", kills="
				+ this.getKills()
				+ ", bossKills="
				+ this.getBossKills()
				+ ", playTime="
				+ this.getPlayTime()
				+ "]";
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bossKills == null) ? 0 : bossKills.hashCode());
		result = prime * result + ((experience == null) ? 0 : experience.hashCode());
		result = prime * result + ((kills == null) ? 0 : kills.hashCode());
		result = prime * result + ((netWorth == null) ? 0 : netWorth.hashCode());
		result = prime * result + ((playTime == null) ? 0 : playTime.hashCode());
		result = prime * result + ((scoreId == null) ? 0 : scoreId.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Score other = (Score) obj;
		if (bossKills == null) {
			if (other.bossKills != null) {
				return false;
			}
		} else if (!bossKills.equals(other.bossKills)) {
			return false;
		}
		if (experience == null) {
			if (other.experience != null) {
				return false;
			}
		} else if (!experience.equals(other.experience)) {
			return false;
		}
		if (kills == null) {
			if (other.kills != null) {
				return false;
			}
		} else if (!kills.equals(other.kills)) {
			return false;
		}
		if (netWorth == null) {
			if (other.netWorth != null) {
				return false;
			}
		} else if (!netWorth.equals(other.netWorth)) {
			return false;
		}
		if (playTime == null) {
			if (other.playTime != null) {
				return false;
			}
		} else if (!playTime.equals(other.playTime)) {
			return false;
		}
		if (scoreId == null) {
			if (other.scoreId != null) {
				return false;
			}
		} else if (!scoreId.equals(other.scoreId)) {
			return false;
		}
		if (time == null) {
			if (other.time != null) {
				return false;
			}
		} else if (!time.equals(other.time)) {
			return false;
		}
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		return true;
	}
}
