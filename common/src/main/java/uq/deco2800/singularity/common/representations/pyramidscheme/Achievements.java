package uq.deco2800.singularity.common.representations.pyramidscheme;

import java.sql.Timestamp;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author 1Jamster1
 *
 */
//If properties are unknown when (de)serialising JSON, ignore, don't error.
@JsonIgnoreProperties(ignoreUnknown = true)
public class Achievements {

	
	@JsonProperty
	//The UUID of the Statistics
	private String statID = null;
	
	@JsonProperty
	@NotEmpty
	//User's ID, this must already exist
	private String userID;
	
	@JsonProperty
	//Achievement name
	private String achievementName = null;
	
	@JsonProperty
	//Timestamp
	private Timestamp timestamp;
	
	/**
	 * Constructor for Achievements Class. Used for Jackson deserialising to object. 
	 * Will Automatically generate a new UUID for scoreId.
	 */
	public Achievements() {
		// Constructor for Jackson Serialising
	}
	
	/**
	 * Constructor for new object using existing statID
	 * @param statID
	 * 				String form of UUID for statID		
	 */
	public Achievements(String statID) {
		this.statID = statID;
	}
	
	/**
	 * Constructor for Achievements Class
	 * @param statID
	 * @param userID
	 * @param achieveName
	 * @param timestamp
	 */
	public Achievements(String statID, String userID, String achieveName, Timestamp timestamp) {
		this.statID = statID;
		this.userID = userID;
		this.achievementName = achieveName;
		this.timestamp = timestamp;
	}
	
	/**
	 * Gets the statID
	 * @return statID
	 */
	public String getStatID() {
		return this.statID;
	}
	
	/**
	 * Gets the userID
	 * @return userID
	 */
	public String getUserID() {
		return this.userID;
	}
	
	/**
	 * Gets the achievement name
	 * @return achievement name
	 */
	public String getAchievementName() {
		return this.achievementName;
	}
	
	/**
	 * Gets the timestamp
	 * @return timestamp
	 */
	public Timestamp getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Sets the statID
	 * @param value
	 * @return the modified object
	 */
	public Achievements setStatID(String value) {
		this.statID = value;
		return this;
	}
	
	/**
	 * Sets the UserID
	 * @param value
	 * @return the modified object
	 */
	public Achievements setUserID(String value) {
		this.userID = value;
		return this;
	}
	
	/**
	 * Sets the AchievementName
	 * @param value
	 * @return the modified object
	 */
	public Achievements setAchievementName(String value) {
		this.achievementName = value;
		return this;
	}
	
	/**
	 * Updates the time stamp
	 * @param timestamp
	 * 			Timestamp to set to. If null take current time
	 * @return Changed achievements object
	 */
	public Achievements setTimestamp(Object timestamp) {
		if(timestamp instanceof String) {
			this.timestamp = Timestamp.valueOf((String) timestamp);
		} else if(timestamp instanceof Timestamp) {
			this.timestamp = (Timestamp) timestamp;
		} else if(timestamp == null) {
			this.setTimestamp();
		}
		
		return this;
	}
	
	/**
	 * Sets the time stamp variable to the current time
	 * @return Changed achievements object
	 */
	public Achievements setTimestamp() {
		this.timestamp = new Timestamp(System.currentTimeMillis());
		return this;
	}
	
	
	@Override
	public String toString() {
		return "Achievements (StatID: " + this.statID + ", UserID: " 
				+ this.userID + ", name: " + this.achievementName + ")";
	}
	
	@Override
	public int hashCode() {
		final int prime = 17;
		int result = 1;
		result = prime * result + this.statID.hashCode();
		result = prime * result + ((this.userID == null) ? 0: this.userID.hashCode());
		result = prime * result + ((this.achievementName == null) ? 0:
			this.achievementName.hashCode());
		result = prime * result + ((this.timestamp == null) ? 0: this.timestamp.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof Achievements)) {
			return false;
		}
		
		Achievements other = (Achievements) obj;
		
		if(!other.getStatID().equals(statID)) {
			return false;
		}
		if(!other.getUserID().equals(userID)) {
			return false;
		}
		if(!other.getAchievementName().equals(achievementName)) {
			return false;
		}
		if(!other.getTimestamp().equals(timestamp)) {
			return false;
		}
		return true;
	}
	
}
