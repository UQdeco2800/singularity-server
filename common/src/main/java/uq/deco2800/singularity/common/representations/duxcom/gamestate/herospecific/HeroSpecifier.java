package uq.deco2800.singularity.common.representations.duxcom.gamestate.herospecific;

/**
 * Specifies data regarding a particular hero
 *
 * Created by liamdm on 19/10/2016.
 */
public class HeroSpecifier {
    /**
     * Create a new hero specifier
     */
    public HeroSpecifier(HeroType type, String heroUUID, int[] spawn) {
        this.type = type;
        this.heroUUID = heroUUID;
        this.spawn = spawn;
    }

    /**
     * Get the heroes type
     */
    public HeroType getType() {
        return type;
    }

    /**
     * Get the heroes UUID
     */
    public String getHeroUUID() {
        return heroUUID;
    }

    /**
     * Get the X, Y spawn point
     */
    public int[] getSpawn() {
        return spawn;
    }

    /**
     * Possible hero typesthat can be re-created
     */
    public enum HeroType {
        KNIGHT,
        CAVALIER,
        ARCHER
    }

    private HeroType type;
    private String heroUUID;
    private int[] spawn;


    /**
     * Deserializer constructor
     */
    public HeroSpecifier(){

    }
}
