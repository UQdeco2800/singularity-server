package uq.deco2800.singularity.common.representations.pyramidscheme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author 1Jamster1
 *
 */
//If properties are unknown when (de)serialising JSON, ignore, don't error.
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserStatistics extends CommonStatistics{

	@JsonProperty
	//User's level in game
	private String userLevel = null;
	
	/**
	 * Constructor for UserStatistics Class. Used for Jackson deserialising to object. 
	 * Will Automatically generate a new UUID for scoreId.
	 */
	public UserStatistics() {
		// Constructor for Jackson Serialising
		super();
	}
	
	/**
	  * Constructor for new UserStatistics class with all data
	 * @param userLevel
	 * 				User's level (in string form)
	 */
	public UserStatistics(String userLevel) {
		
		super();
		this.userLevel = userLevel;
	}
	
	/**
	 * Gets the User's Level
	 * @return User's Level
	 */
	public String getUserLevel() {
		return this.userLevel;
	}
	
	/**
	 * Updates the user level to the new specified value
	 * @param value
	 * @return Changed Statistics class object
	 */
	public UserStatistics setUserLevel(String value) {
		this.userLevel = value;
		return this;
	}

	/**
	 * Adds the designated value to the user level
	 * @param value (int), the value to be added
	 * @return Changed Statistics class object
	 */
	public UserStatistics addToUserLevel(int value) {
		int temp;
		
		temp = Integer.parseInt(this.userLevel) + value;
		this.userLevel = Integer.toString(temp);
		
		return this;
	}
	
	/**
	 * Adds the designated value to the user level
	 * @param value (string), the value to be added
	 * @return Changed Statistics class object
	 */
	public UserStatistics addToUserLevel(String value) {
		return this.addToUserLevel(Integer.parseInt(value));
	}
	

	/**
	 * Merges this userStats with the given userStats
	 * @param otherStats, the statistics object to be merged with
	 * @return Changed Statistics class object
	 */
	public UserStatistics mergeStatistics(UserStatistics otherStats) {
		
		this.addToUserLevel(otherStats.getUserLevel());
		this.addToMinionsPlayed(otherStats.getMinionsPlayed());
		this.addToMinionsKilled(otherStats.getMinionsKilled());
		this.addToMinionsLost(otherStats.getMinionsLost());
		this.addToHealthLost(otherStats.getHealthLost());
		this.addToHealthTaken(otherStats.getHealthTaken());
		this.addToTotalWins(otherStats.getTotalWins());
		this.addToTotalLosses(otherStats.getTotalLosses());
		this.addToTotalHours(otherStats.getTotalHours());
		this.addToTotalMinutes(otherStats.getTotalMinutes());
		
		return this;
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
	@Override
	public String toString() {
		return "UserStats = (StatID: " + this.getStatID() + 
				", UserID: " + this.getUserID() + 
				", UserLevel: " + this.userLevel +
				"\nMinionsPlayed: " + this.getMinionsPlayed() + 
				", MinionsKilled: " + this.getMinionsKilled() + 
				", MinionsLost: " + this.getMinionsLost() +
				"\nHealthLost: " + this.getHealthLost() + 
				", HealthTaken: " + this.getHealthTaken() + 
				", LastLogin: " + this.getLastLogin().toString() + 
				"\nTotalWins: " + this.getTotalWins() +
				", TotalLosses: " + this.getTotalLosses() +
				", TotalHours: " + this.getTotalHours() +
				", TotalMinutes: " + this.getTotalMinutes() +
				")";
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
	@Override 
	public int hashCode() {
		final int prime = 17;
		int result;
		result = super.hashCode();
		result = prime * result + ((this.userLevel == null) ? 0: this.userLevel.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)) {
			return false;
		}
		if(obj == this) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(! (obj instanceof UserStatistics)) {
			return false;
		}
		
		UserStatistics checking = (UserStatistics) obj;
		
		if(!checking.getUserLevel().equals(this.userLevel)) {
			return false;
		}
		
		
		return true;
	}
}
