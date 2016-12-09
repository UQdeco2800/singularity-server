package uq.deco2800.singularity.common.representations.duxcom.gamestate.herospecific;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Specifies data regarding a squad owned by a player
 * Created by liamdm on 19/10/2016.
 */
public class SquadSpecifier {
    private String owner;
    private LinkedList<HeroSpecifier> heroes;

    /**
     * Creates a squad specifier
     * @param owner the owner of the squad
     * @param heroSpecifiers the hero specifiers to add (nullable)
     */
    public SquadSpecifier(String owner, HeroSpecifier ... heroSpecifiers) {
        this.owner = owner;
        heroes = new LinkedList<>();
        Collections.addAll(heroes, heroSpecifiers);
    }

    /**
     * Get the hero list
     */
    public List<HeroSpecifier> getHeroes(){
        return heroes;
    }

    /**
     * Add a single hero
     * @param heroSpecifier the hero specifier to add
     */
    public void add(HeroSpecifier heroSpecifier){
        heroes.add(heroSpecifier);
    }

    /**
     * Add all the heroes
     * @param heroSpecifierList the list of heroes
     */
    public void addAll(List<HeroSpecifier> heroSpecifierList){
        for(HeroSpecifier heroSpecifier : heroSpecifierList){
            add(heroSpecifier);
        }
    }

    public String getOwner() {
        return owner;
    }

    /**
     * Deserializer constructor
     */
    public SquadSpecifier(){

    }
}
