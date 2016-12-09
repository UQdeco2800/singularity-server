package uq.deco2800.singularity.common.representations.duxcom;

import java.sql.Timestamp;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jhess-osum
 *
 */
//If properties are unknown when (de)serialising JSON, ignore, don't error.
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerStats {
    
    @JsonProperty
    // Id of the current score. Generated via UUID.randomUUID().toString()
    // Should only be generated when adding new Score. 
    private String scoreId = null;
    
    @JsonProperty
    @NotEmpty
    // UserID for given player stat, user must already exist
    private String userId = null;
    
    @JsonProperty
    // Date of most recent save
    // On create new set to current datetime
    private Timestamp timestamp = null;
    
    @JsonProperty
    // Number of in-game kills
    private String kills = null;
    
    @JsonProperty
    // Current level the player is on
    private String score = null;
    
    
    /**
     * Constructor for a PlayerStats class. Used for Jackson deserialising to object.
     * Will automatically generate a new UUID for the scoreId.
     * @param string 
     */
    public PlayerStats() {
        // Constructor for Jackson Serialising
    }
    
    /**
     * Constructor to make a PlayerStats class from an existing UUID and userId
     * 
     * @param scoreId
     *            The score's ID. A string representation of a UUID    
     *         
     */
    public PlayerStats(String scoreId) {
        this.scoreId = scoreId;
    
    }
    
    /**
     * Constructor to make Score class.
     * 
     * @param scoreId
     * @param userId
     * @param timestamp
     * @param kills
     * @param score
     */
    public PlayerStats(String scoreId, String userId, String timestamp, String kills, String score) {
        this.scoreId = scoreId;
        this.userId = userId;
        this.timestamp = Timestamp.valueOf(timestamp);
        this.kills = kills;
        this.score = score;
        
    }
    
    
    /**
     * Retrieves the scores's UUID in String form.
     * 
     * @return the scoreId
     */
    public String getScoreId() {
        return scoreId;
    }
    
    /**
     * Retrieves the user's UUID in String form.
     * 
     * @return the UserId
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * Retrieves the timestamp
     * 
     * @return the timestamp as a string
     */
    public String getTimestamp() {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toString();
    }
    
    /**
     * Retrieves player stats type
     * 
     * @return player stats type
     */
    public String getKills() {
        return kills;
    }
    
    /**
     * Retrieves the user's score
     * 
     * @return score
     */
    public String getScore() {
        return score;
    }
    
    
    /**
     * Sets scoreId
     * 
     * @param scoreId
     * @return The updated PlayerStats object
     */
    public PlayerStats setScoreId(String scoreId) {
        this.scoreId = scoreId;
        return this;
    }
    
    /**
     * Sets userId
     * 
     * @param userId
     * @return The updated PlayerStats object
     */
    public PlayerStats setUserId(String userId) {
        this.userId = userId;
        return this;
    }
    
    /**
     * Sets timestamp
     * 
     * @param timestamp
     * @return The updated PlayerStats object
     */
    public PlayerStats setTimestamp(Object timestamp) {
        
        if (timestamp instanceof String) {
            this.timestamp = Timestamp.valueOf((String) timestamp);
        } else if (timestamp instanceof Timestamp) {
            this.timestamp = (Timestamp)timestamp;
        }
        
        if (timestamp == null) {
            setNewTimestamp();
        }
        
        return this;
    }
    
    /**
     * Sets new timestamp
     * 
     * @param timestamp
     * @return The updated PlayerStats object
     */
    public PlayerStats setNewTimestamp() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        return this;
    }
    
    /**
     * Sets Number of Kills
     * 
     * @param type
     * @return the updated PlayerStats object
     */
    public PlayerStats setKills(String kills){
        this.kills = kills;
        return this;
    }
    
    /**
     * Sets score
     * 
     * @param score
     * @return The updated PlayerStats object
     */
    public PlayerStats setScore(String score) {
        this.score = score;
        return this;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Score [scoreId=" + this.scoreId 
                + ", userId="  + this.userId
                + ", timestamp=" + this.timestamp.toString()
                + ", kills=" + this.kills
                + ", score="  + this.score
                + "]";
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((score == null) ? 0 : score.hashCode());
        result = prime * result + ((kills == null) ? 0 :  kills.hashCode());
        result = prime * result + ((scoreId == null) ? 0 : scoreId.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof PlayerStats))
            return false;
        PlayerStats other = (PlayerStats) obj;
        if (score == null) {
            if (other.score != null)
                return false;
        } else if (!score.equals(other.score))
            return false;
        if (kills == null) {
            if (other.kills != null)
                return false;
        } else if (!kills.equals(other.kills))
            return false;
        if (scoreId == null) {
            if (other.scoreId != null)
                return false;
        } else if (!scoreId.equals(other.scoreId))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }

    

}
