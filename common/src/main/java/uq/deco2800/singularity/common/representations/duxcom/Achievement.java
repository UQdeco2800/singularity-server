package uq.deco2800.singularity.common.representations.duxcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Gormly on 14/09/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Achievement {

    @JsonProperty
    // The ID of an achievement. Generated via UUID.randomUUID().toString().
    // Should only be generated when getting a new Achievement from the AchievementRegister.
    private String id = null;

    @JsonProperty
    @NotEmpty
    // Title of the Achievement. Must be unique system wide.
    private String name = null;

    @JsonProperty
    @NotEmpty
    // An achievement's description.
    private String desciption = null;

    @JsonProperty
    @NotEmpty
    // An achievement's type
    private AchievementType type = null;

    @JsonProperty
    // An achievement's score
    private int score = 0;

    /**
     * Constructor for a Achievement class. Used for Jackson deserialising to object.
     * Will automatically generate a new UUID for the achievement's ID.
     */
    public Achievement() {
        // Constructor for Jackson Serialising
    }
    /**
     * Creates a new user instance auto filling details.
     *
     * @param name
     *            The achievement's requested title. Must not be empty.
     * @param description
     *            The achievement's description
     * @param type
     *            The achievement's type
     * @param score
     *            The achievement's completion score.
     */
    public Achievement(String id, String name, String description, AchievementType type, int score) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.desciption = description;
        this.score = score;
    }

    /**
     * Returns the Achievement's Id.
     *
     * @return id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the Achievment's Id.
     *
     * @param id
     * @return
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the unique achievement name.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the achievements name. This must be unique otherwise it will fail.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of the achievement.
     *
     * @return description.
     */
    public String getDescription() {
        return desciption;
    }

    /**
     * Sets the description of the achievement.
     *
     * @param desciption
     */
    public void setDescription(String desciption) {
        this.desciption = desciption;
    }

    /**
     * Gets the Achievement type of the achievement.
     *
     * @return type.
     */
    public AchievementType getType() {
        return type;
    }

    /**
     * Sets the achievement type.
     *
     * @param type
     */
    public void setType(AchievementType type) {
        this.type = type;
    }

    /**
     * Gets the score required to complete the achievement.
     *
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score required to complete the achievement.
     *
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Checks if both achievements are equivalent.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Achievement)) return false;

        Achievement that = (Achievement) o;

        if (getScore() != that.getScore()) return false;
        if (!getId().equals(that.getId())) return false;
        if (!getName().equals(that.getName())) return false;
        if (!desciption.equals(that.desciption)) return false;
        return getType() == that.getType();

    }

    /**
     * Generate a hashcode for the achievement.
     *
     * @return int, unique code associated with the achievement.
     */
    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + desciption.hashCode();
        result = 31 * result + getType().hashCode();
        result = 31 * result + getScore();
        return result;
    }
    
    @Override
    public String toString() {
        return "Achievement [ID: " + this.id
                + ", Name: " + this.name
                + ", Type: " + this.type
                + ", Description: " + this.desciption
                + ", Score: " + this.score;
    }
}
