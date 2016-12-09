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
public class CommonStatistics {

	
	@JsonProperty
	//The UUID of the Statistics
	private String statID = null;
	
	@JsonProperty
	@NotEmpty
	//User's ID, this must already exist
	private String userID;
	
	@JsonProperty
	//User's total minions played
	private String minionsPlayed = null;
	
	@JsonProperty
	//User's total minions killed
	private String minionsKilled = null;
	
	@JsonProperty
	//User's total minions lost
	private String minionsLost = null;
	
	@JsonProperty
	//User's total health lost
	private String healthLost = null;
	
	@JsonProperty
	//User's total health taken from opponent 
	private String healthTaken = null;
	
	@JsonProperty
	//User's last login time (Updates after new login)
	private Timestamp lastLogin = null;
	
	@JsonProperty
	//User's total wins
	private String totalWins = null;
	
	@JsonProperty
	//User's total losses
	private String totalLosses = null;
		
	@JsonProperty
	//User's total hours played
	private String totalHours = null;
	
	@JsonProperty
	//User's total minutes played
	private String totalMinutes = null;
	
	/**
	 * Constructor for UserStatistics Class. Used for Jackson deserialising to object. 
	 * Will Automatically generate a new UUID for scoreId.
	 */
	public CommonStatistics() {
		// Constructor for Jackson Serialising
	}
	
	/**
	 * Constructor for new object using existing statID
	 * @param statID
	 * 				String form of UUID for statID		
	 */
	public CommonStatistics(String statID) {
		this.statID = statID;
	}
	
	/**
	  * Constructor for new UserStatistics class with all data
	 * @param statID
	 * 				The UUID of the Statistics
	 * @param userId
	 * 				User's id
	 * @param userLevel
	 * 				User's level (in string form)
	 * @param minionsPlayed
	 * 				User's total minions played (in string form)
	 * @param minionsKilled
	 * 				User's total minions killed (in string form)
	 * @param minionsLost
	 * 				User's total minions lost (in string form)
	 * @param healthLost
	 * 				User's total health lost (in string form)
	 * @param healthTaken
	 * 				User's total health taken from opponent (in string form)
	 * @param lastLogin
	 * 				User's last login time (in string form)
	 * @param totalWins
	 * 				User's total Wins (in string form)
	 * @param totalLosses
	 * 				User's total losses (in string form)
	 * @param totalHours
	 * 				User's total play time hour component (in string form)
	 * @param totalMinutes
	 * 				User's total play time minutes component (in string form)
	 */
	public CommonStatistics(String statID, String userID, String minionsPlayed, String minionsKilled,
				String minionsLost, String healthLost, String healthTaken) {
		
		this.statID = statID;
		this.userID = userID;
		this.minionsPlayed = minionsPlayed;
		this.minionsKilled = minionsKilled;
		this.minionsLost = minionsLost;
		this.healthLost = healthLost;
		this.healthTaken = healthTaken;
	}
	
	/**
	 * Gets the Stat ID
	 * @return Stat ID
	 */
	public String getStatID() {
		return this.statID;
	}
	
	/**
	 * Gets the User's ID
	 * @return User's ID
	 */
	public String getUserID() {
		return this.userID;
	}
	
	/**
	 * Gets the User's total Minions played
	 * @return Minions played
	 */
	public String getMinionsPlayed() {
		return this.minionsPlayed;
	}
	
	/**
	 * Gets the User's total Minions killed
	 * @return Minions killed
	 */
	public String getMinionsKilled() {
		return this.minionsKilled;
	}
	
	/**
	 * Gets the User's total Minions lost
	 * @return Minions lost
	 */
	public String getMinionsLost() {
		return this.minionsLost;
	}
	
	/**
	 * Gets the User's total health lost
	 * @return Health lost
	 */
	public String getHealthLost() {
		return this.healthLost;
	}
	
	/**
	 * Gets the User's total health taken
	 * @return Health taken
	 */
	public String getHealthTaken() {
		return this.healthTaken;
	}
	
	/**
	 * Gets the time stamp for the last login time
	 * @return Last login time stamp
	 */
	public Timestamp getLastLogin() {
		if(this.lastLogin == null) {
			this.setLastLogin();
		}
		return this.lastLogin;
	}
	
	/**
	 * Gets the User's total wins
	 * @return total Wins
	 */
	public String getTotalWins() {
		return this.totalWins;
	}
	
	/**
	 * Gets the User's total Losses
	 * @return total losses
	 */
	public String getTotalLosses() {
		return this.totalLosses;
	}
	
	/**
	 * Gets the User's play time (hours component)
	 * @return play time (hours component)
	 */
	public String getTotalHours() {
		return this.totalHours;
	}
	
	/**
	 * Gets the User's play time (minutes component)
	 * @return play time (minutes component)
	 */
	public String getTotalMinutes() {
		return this.totalMinutes;
	}
	
	/**
	 * Updates the stat id to the new specified value
	 * @param value
	 * @return Changed Statistics class object
	 */
	public Object setStatID(String value) {
		this.statID = value;
		return this.getClass();
	}
	
	/**
	 * Updates the user id to the new specified value
	 * @param value
	 * @return Changed Statistics class object
	 */
	public Object setUserID(String value) {
		this.userID = value;
		return this.getClass();
	}
	
	/**
	 * Updates the total minions played to the specified value
	 * @param value
	 * @return Changed Statistics class object
	 */
	public Object setMinionsPlayed(String value) {
		this.minionsPlayed = value;
		return this.getClass();
	}
	
	/**
	 * Updates the total minions killed to the specified value
	 * @param value
	 * @return Changed Statistics class object
	 */
	public Object setMinionsKilled(String value) {
		this.minionsKilled = value;
		return this.getClass();
	}
	
	/**
	 * Updates the total minions lost to the specified value
	 * @param value
	 * @return Changed Statistics class object
	 */
	public Object setMinionsLost(String value) {
		this.minionsLost = value;
		return this.getClass();
	}
	
	/**
	 * Updates the total health lost to the specified value
	 * @param value
	 * @return Changed Statistics class object
	 */
	public Object setHealthLost(String value) {
		this.healthLost = value;
		return this.getClass();
	}
	
	/**
	 * Updates the total health taken to the specified value
	 * @param value
	 * @return Changed Statistics class object
	 */
	public Object setHealthTaken(String value) {
		this.healthTaken = value;
		return this.getClass();
	}
	
	/**
	 * Updates the last login time stamp
	 * @param timestamp
	 * 			Timestamp to set to. If null take current time
	 * @return Changed Statistics class object
	 */
	public Object setLastLogin(Object timestamp) {
		if(timestamp instanceof String) {
			this.lastLogin = Timestamp.valueOf((String) timestamp);
		} else if(timestamp instanceof Timestamp) {
			this.lastLogin = (Timestamp) timestamp;
		} else if(timestamp == null) {
			this.setLastLogin();
		}
		
		return this.getClass();
	}
	
	/**
	 * Sets the lastLogin variable to the current time
	 * @return Changed Statistics class object
	 */
	public Object setLastLogin() {
		this.lastLogin = new Timestamp(System.currentTimeMillis());
		return this.getClass();
	}
	
	/**
	 * Sets the User's total wins
	 * @param value
	 * @return Changed Statistics class object
	 */
	public Object setTotalWins(String value) {
		this.totalWins = value;
		return this.getClass();
	}
	
	/**
	 * Sets the User's total Losses
	 * @param value
	 * @return Changed Statistics class object
	 */
	public Object setTotalLosses(String value) {
		this.totalLosses = value;
		return this.getClass();
	}
	
	/**
	 * Sets the User's play time (hours component)
	 * @param value
	 * @return Changed Statistics class object
	 */
	public Object setTotalHours(String value) {
		this.totalHours = value;
		return this.getClass();
	}
	
	/**
	 * Sets the User's play time (minutes component)
	 * @param value
	 * @return Changed Statistics class object
	 */
	public Object setTotalMinutes(String value) {
		this.totalMinutes = value;
		return this.getClass();
	}
	
	/**
	 * Adds the designated value to the minions played
	 * @param value (int), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToMinionsPlayed(int value) {
		int temp;
		
		temp = Integer.parseInt(this.minionsPlayed) + value;
		this.minionsPlayed = Integer.toString(temp);
		
		return this.getClass();
	}
	
	/**
	 * Adds the designated value to the minions played
	 * @param value (string), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToMinionsPlayed(String value) {
		return this.addToMinionsPlayed(Integer.parseInt(value));
	}
	
	/**
	 * Adds the designated value to the minions killed
	 * @param value (int), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToMinionsKilled(int value) {
		int temp;
		
		temp = Integer.parseInt(this.minionsKilled) + value;
		this.minionsKilled = Integer.toString(temp);
		
		return this.getClass();
	}
	
	/**
	 * Adds the designated value to the minions killed
	 * @param value (string), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToMinionsKilled(String value) {
		return this.addToMinionsKilled(Integer.parseInt(value));
	}
	
	/**
	 * Adds the designated value to the minions lost
	 * @param value (int), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToMinionsLost(int value) {
		int temp;
		
		temp = Integer.parseInt(this.minionsLost) + value;
		this.minionsLost = Integer.toString(temp);
		
		return this.getClass();
	}
	
	/**
	 * Adds the designated value to the minions lost
	 * @param value (string), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToMinionsLost(String value) {
		return this.addToMinionsLost(Integer.parseInt(value));
	}
	
	/**
	 * Adds the designated value to the health lost
	 * @param value (int), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToHealthLost(int value) {
		int temp;
		
		temp = Integer.parseInt(this.healthLost) + value;
		this.healthLost = Integer.toString(temp);
		
		return this.getClass();
	}
	
	/**
	 * Adds the designated value to the health lost
	 * @param value (string), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToHealthLost(String value) {
		return this.addToHealthLost(Integer.parseInt(value));
	}
	
	/**
	 * Adds the designated value to the health taken
	 * @param value (int), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToHealthTaken(int value) {
		int temp;
		
		temp = Integer.parseInt(this.healthTaken) + value;
		this.healthTaken = Integer.toString(temp);
		
		return this.getClass();
	}
	
	/**
	 * Adds the designated value to the health taken
	 * @param value (string), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToHealthTaken(String value) {
		return this.addToHealthTaken(Integer.parseInt(value));
	}
	
	/**
	 * Adds the designated value to the total wins
	 * @param value (int), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToTotalWins(int value) {
		int temp;
		
		temp = Integer.parseInt(this.totalWins) + value;
		this.totalWins = Integer.toString(temp);
		
		return this;
	}
	
	/**
	 * Adds the designated value to the total wins
	 * @param value (string), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToTotalWins(String value) {
		return this.addToTotalWins(Integer.parseInt(value));
	}
	
	/**
	 * Adds the designated value to the total losses
	 * @param value (int), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToTotalLosses(int value) {
		int temp;
		
		temp = Integer.parseInt(this.totalLosses) + value;
		this.totalLosses = Integer.toString(temp);
		
		return this.getClass();
	}
	
	/**
	 * Adds the designated value to the total losses
	 * @param value (string), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToTotalLosses(String value) {
		return this.addToTotalLosses(Integer.parseInt(value));
	}
	
	/**
	 * Adds the designated value to the total hours
	 * @param value (int), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToTotalHours(int value) {
		int temp;
		
		temp = Integer.parseInt(this.totalHours) + value;
		this.totalHours = Integer.toString(temp);
		
		return this.getClass();
	}
	
	/**
	 * Adds the designated value to the total hours
	 * @param value (string), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToTotalHours(String value) {
		return this.addToTotalHours(Integer.parseInt(value));
	}
	
	/**
	 * Adds the designated value to the total minutes
	 * @param value (int), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToTotalMinutes(int value) {
		int temp;
		
		temp = Integer.parseInt(this.totalMinutes) + value;
		this.totalMinutes = Integer.toString(temp);
		
		return this.getClass();
	}
	
	/**
	 * Adds the designated value to the total minutes
	 * @param value (string), the value to be added
	 * @return Changed Statistics class object
	 */
	public Object addToTotalMinutes(String value) {
		return this.addToTotalMinutes(Integer.parseInt(value));
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
	@Override
	public String toString() {
		return "CommonStats = (StatID: " + this.statID + 
				", UserID: " + this.userID + 
				"\nMinionsPlayed: " + this.minionsPlayed + 
				", MinionsKilled: " + this.minionsKilled + 
				", MinionsLost: " + this.minionsLost +
				"\nHealthLost: " + this.healthLost + 
				", HealthTaken: " + this.healthTaken + 
				", LastLogin: " + this.lastLogin.toString() + 
				"\nTotalWins: " + this.totalWins +
				", TotalLosses: " + this.totalLosses +
				", TotalHours: " + this.totalHours +
				", TotalMinutes: " + this.totalMinutes +
				")";
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
	@Override 
	public int hashCode() {
		final int prime = 17;
		int result = 1;
		result = prime * result + this.statID.hashCode();
		result = prime * result + ((this.userID == null) ? 0: this.userID.hashCode());
		result = prime * result + ((this.minionsPlayed == null) ? 0: this.minionsPlayed.hashCode());
		result = prime * result + ((this.minionsKilled == null) ? 0:this.minionsKilled.hashCode());
		result = prime * result + ((this.minionsLost == null) ? 0: this.minionsLost.hashCode());
		result = prime * result + ((this.healthLost == null) ? 0: this.healthLost.hashCode());
		result = prime * result + ((this.healthTaken == null) ? 0: this.healthTaken.hashCode());
		result = prime * result + ((this.lastLogin == null) ? 0: this.lastLogin.hashCode());
		result = prime * result + ((this.totalWins == null) ? 0: this.totalWins.hashCode());
		result = prime * result + ((this.totalLosses == null) ? 0: this.totalLosses.hashCode());
		result = prime * result + ((this.totalHours == null) ? 0: this.totalHours.hashCode());
		result = prime * result + ((this.totalMinutes == null) ? 0: this.totalMinutes.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(! (obj instanceof CommonStatistics)) {
			return false;
		}
		
		CommonStatistics checking = (CommonStatistics) obj;
		
		if(!checking.getStatID().equals(this.statID)) {
			return false;
		}
		if(!checking.getUserID().equals(this.userID)) {
			return false;
		}
		if(!checking.getMinionsPlayed().equals(this.minionsPlayed)) {
			return false;
		}
		if(!checking.getMinionsKilled().equals(this.minionsKilled)) {
			return false;
		}
		if(!checking.getMinionsLost().equals(this.minionsLost)) {
			return false;
		}
		if(!checking.getHealthLost().equals(this.healthLost)) {
			return false;
		}
		if(!checking.getHealthTaken().equals(this.healthTaken)) {
			return false;
		}
		if(!checking.getLastLogin().equals(this.lastLogin)) {
			return false;
		}
		if(!checking.getTotalWins().equals(this.totalWins)) {
			return false;
		}
		if(!checking.getTotalLosses().equals(this.totalLosses)) {
			return false;
		}
		if(!checking.getTotalHours().equals(this.totalHours)) {
			return false;
		}
		if(!checking.getTotalMinutes().equals(this.totalMinutes)) {
			return false;
		}
		
		return true;
	}
}
