package uq.deco2800.singularity.common.representations.pyramidscheme;

import java.sql.Timestamp;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author tris10au
 *
 */
public class GameState {

	@JsonProperty
	// The UUID of the game state
	private String stateID = null;
	
	@JsonProperty
	@NotEmpty
	// User's ID, this must already exist
	private String userID;
	
	@JsonProperty
	@NotEmpty
	// The time this version of the game state was created
	private Timestamp saveTime = null;
	
	@JsonProperty
	@NotEmpty
	// The data to save for the game
	private String data;
	
	/**
	 * Constructor for UserStatistics Class. Used for Jackson deserialising to object. 
	 * Will Automatically generate a new UUID for scoreId.
	 */
	public GameState() {
		// Constructor for Jackson Serialising
	}
	
	/**
	 * Constructor for new object using existing stateID
	 * @param stateID
	 * 				String form of UUID for stateID		
	 */
	public GameState(String stateID) {
		this.stateID = stateID;
	}
	
	/**
	 * Constructor for new GameState class with all data
	 * @param stateID
	 * 				The UUID of the Statistics
	 * @param userId
	 * 				User's id
	 * @param saveTime
	 * 				The time the save occurred.
	 * @param data
	 * 				A JSON string of game data that is saved
	 */
	public GameState(String stateID, String userID, String saveTime, String data) {
		
		this.stateID = stateID;
		this.userID = userID;
		this.saveTime = Timestamp.valueOf(saveTime);
		this.data = data;
	}
	
	/**
	 * Gets the GameState ID
	 * @return GameState ID
	 */
	public String getStateID() {
		return this.stateID;
	}
	
	/**
	 * Gets the User's ID
	 * @return User's ID
	 */
	public String getUserID() {
		return this.userID;
	}
	
	/**
	 * Gets the time stamp the state was saved
	 * @return Time stamp when game was saved
	 */
	public Timestamp getSaveTime() {
		if(this.saveTime == null) {
			this.setSaveTime();
		}
		return this.saveTime;
	}
	
	/**
	 * Gets the data of the game state
	 * @return A string of JSON data of the game state
	 */
	public String getData() {
		return this.data;
	}
	
	/**
	 * Updates the state id to the new specified value
	 * @param value
	 * @return Changed GameState class object
	 */
	public GameState setStateID(String value) {
		this.stateID = value;
		return this;
	}
	
	/**
	 * Updates the user id to the new specified value
	 * @param value
	 * @return Changed GameState class object
	 */
	public GameState setUserID(String value) {
		this.userID = value;
		return this;
	}
	
	/**
	 * Updates the save time for this state
	 * @param timestamp
	 * 			Timestamp to set to. If null, take current time
	 * @return Changed GameState class object
	 */
	public GameState setSaveTime(Object timestamp) {
		if(timestamp instanceof String) {
			this.saveTime = Timestamp.valueOf((String) timestamp);
		} else if(timestamp instanceof Timestamp) {
			this.saveTime = (Timestamp) timestamp;
		} else if(timestamp == null) {
			this.setSaveTime();
		}
		
		return this;
	}
	
	/**
	 * Sets the save time variable to the current time
	 * @return Changed GameState class object
	 */
	public GameState setSaveTime() {
		this.saveTime = new Timestamp(System.currentTimeMillis());
		return this;
	}
	
	/**
	 * Sets the data to a new JSON string of data
	 * @return Changed GameState class object
	 */
	public GameState setData(String data) {
		this.data = data;
		return this;
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
	@Override
	public String toString() {
		return "GameState = (StateID: " + this.stateID + 
				", UserID: " + this.userID + 
				", SaveTime: " + this.saveTime.toString() + 
				", Data: " + this.data + 
				")";
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
	@Override 
	public int hashCode() {
		final int prime = 17;
		int result = 1;
		result = prime * result + this.stateID.hashCode();
		result = prime * result + ((this.userID == null) ? 0: this.userID.hashCode());
		result = prime * result + ((this.saveTime == null) ? 0: this.saveTime.hashCode());
		result = prime * result + ((this.data == null) ? 0: this.data.hashCode());
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
		if(! (obj instanceof GameState)) {
			return false;
		}
		GameState checking = (GameState) obj;
		if(!checking.getStateID().equals(this.stateID)) {
			return false;
		}
		if(!checking.getUserID().equals(this.userID)) {
			return false;
		}
		if(!checking.getSaveTime().equals(this.saveTime)) {
			return false;
		}
		if(!checking.getData().equals(this.data)) {
			return false;
		}
		
		return true;
	}
}
