package uq.deco2800.singularity.common.representations.duxcom.gamestate.herospecific;

/**
 * A hero targeting another hero
 *
 * Created by liamdm on 19/10/2016.
 */
public class HeroTarget {

    /**
     * The origin of the hero change
     */
    private String origin;
    /**
     * The reciever of the hero change
     */
    private String receiver;

    public HeroTarget(String origin, String receiver) {
        this.origin = origin;
        this.receiver = receiver;
    }

    /**
     * Get the hero who caused the effect
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Get the reciever of the hero effect
     */
    public String getReceiver() {
        return receiver;
    }


    /**
     * Deserializer constructor
     */
    public HeroTarget(){

    }
}
