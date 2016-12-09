package uq.deco2800.singularity.common.representations.pyramidscheme;

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
public class ChampionStatistics extends CommonStatistics{

	@JsonProperty
	@NotEmpty
	//Champion's name
	private String champName;
	
	/**
	 * Constructor for UserStatistics Class. Used for Jackson deserialising to object. 
	 * Will Automatically generate a new UUID for scoreId.
	 */
	public ChampionStatistics() {
		// Constructor for Jackson Serialising
		super();
	}
	
	/**
	 * Constructor for new UserStatistics class with all data
	 *	 @param champName
	 * 				Champions Name
	 */
	public ChampionStatistics(String champName) {
		
		super();
		this.champName = champName;
	}
	
	/**
	 * Gets the Champions Name
	 * @return Champions Name
	 */
	public String getChampName() {
		return this.champName;
	}
	
	/**
	 * Updates the champion name to the new specified value
	 * @param value
	 * @return Changed Statistics class object
	 */
	public ChampionStatistics setChampName(String value) {
		this.champName = value;
		return this;
	}
	
	/**
	 * Merges this champStats with the given champStats
	 * @param otherStats, the statistics object to be merged with
	 * @return Changed Statistics class object
	 */
	public ChampionStatistics mergeStatistics(ChampionStatistics otherStats) {
		
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
		return "ChampStats = (StatID: " + this.getStatID() + 
				", UserID: " + this.getUserID() + 
				", ChampionName: " + this.champName +
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
		result = prime * result + ((this.champName == null) ? 0: this.champName.hashCode());
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
		if(! (obj instanceof ChampionStatistics)) {
			return false;
		}
		
		ChampionStatistics checking = (ChampionStatistics) obj;
		
		if(!checking.getChampName().equals(this.champName)) {
			return false;
		}
		
		return true;
	}
}
