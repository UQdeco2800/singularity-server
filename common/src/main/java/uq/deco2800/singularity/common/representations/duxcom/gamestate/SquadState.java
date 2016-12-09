package uq.deco2800.singularity.common.representations.duxcom.gamestate;

import uq.deco2800.singularity.common.representations.duxcom.gamestate.herospecific.HeroTarget;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.herospecific.SquadSpecifier;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Sends messges regarding a current players squad state
 * Created by liamdm on 19/10/2016.
 */
public class SquadState extends AbstractGameStateMessage {
    /**
     * The squad state message type
     */
    public enum MessageType {
        /**
         * Sent by the administrator and contains the squad initialisation parameters
         * for all the client games
         */
        SQUAD_INIT,
        /**
         * Denotes a change to the next hero specified
         */
        NEXT_HERO,
        /**
         * Contains data regarding a hero attack which will
         * reduce the health of a given hero
         */
        HERO_ATTACK,
        /**
         * A particular hero is now kill RIP
         */
        HERO_DEATH,
        /**
         * Arbitrary hero health change
         */
        HERO_HEALTH_CHANGE,
        /**
         * The server is killing off a squads hero
         * to maintain game state
         */
        WORLD_KILL
    }

    /**
     * Kills an arbitrary hero
     * @param heroUUID the UUID of the heor
     */
    public static SquadState systemKillHero(String heroUUID){
        return new SquadState(MessageType.WORLD_KILL, heroUUID);
    }

    /**
     * Dentoes a changing of an arbitrary heroes health
     */
    public static SquadState heroHealthChange(String heroUUID, double newHealth){
        return new SquadState(MessageType.HERO_HEALTH_CHANGE, heroUUID, newHealth);
    }

    /**
     * Denotes the death of a squad hero
     * @param heroUUID the UUID of the hero
     */
    public static SquadState heroDeath(String heroUUID){
        return new SquadState(MessageType.HERO_DEATH, heroUUID);
    }


    /**
     * The inner message type
     */
    private MessageType innerMessageType;

    /**
     * Get the inner message type
     */
    public MessageType getInnerMessageType(){
        return innerMessageType;
    }

    /**
     * The string payload
     */
    private String payloadString;
    private Double payloadDouble;

    /**
     * The move target
     */
    private HeroTarget target;

    /**
     * The specifier payload
     */
    private List<SquadSpecifier> specifiers;

    /**
     * If this message is a targetable message
     */
    private boolean targetable = false;

    /**
     * The squad state constructor for non-targetable
     * two payload messages
     * @param type the type of the message
     * @param payload the payload of the message
     * @param numericalPayload the numerical payload to include.
     */
    private SquadState(MessageType type, String payload, Double numericalPayload){
        this.innerMessageType = type;
        this.payloadString = payload;
        this.payloadDouble = numericalPayload;
    }

    public static SquadState attack(String attacker, String attacked, double damage, String effect){
        HeroTarget heroTarget = new HeroTarget(attacker, attacked);
        return new SquadState(MessageType.HERO_ATTACK, heroTarget, effect, damage);
    }

    /**
     * Denotes a change to the next hero
     * @param heroUUID the UUID of the hero to change to
     */
    public static SquadState nextHero(String heroUUID){
        return new SquadState(MessageType.NEXT_HERO, heroUUID);
    }

    /**
     * Creates a targetable squad state message
     * @param type the type of the message
     * @param target the targeting
     * @param payload the payload
     */
    private SquadState(MessageType type, HeroTarget target, String payload, Double numericalValue){
        this.innerMessageType = type;
        this.targetable = true;
        this.target = target;
        this.payloadString = payload;
        this.payloadDouble = numericalValue;
    }

    /**
     * Create a squad state message with static string payload
     * @param payload the attached payload
     */
    private SquadState(MessageType type, String payload){
        this.innerMessageType = type;
        this.payloadString = payload;
    }

    /**
     * Broadcast the squad state pre-game
     */
    public static SquadState broadcastSquadState(List<SquadSpecifier> squadState){
        return new SquadState(squadState);
    }

    /**
     * Creates a squad state message to initialise the squad state
     */
    private SquadState(List<SquadSpecifier> specifierLinkedList){
        this.innerMessageType = MessageType.SQUAD_INIT;
        this.specifiers = specifierLinkedList;
    }

    /**
     * The deseralizor constructor
     */
    public SquadState(){

    }

    @Override
    public GameStateMessageType getMessageType() {
        return GameStateMessageType.SQUAD_STATE;
    }

    /**
     * Returns the squad specifiers with a hash map
     * of the owner name to the specifier
     */
    public List<SquadSpecifier> getSquadSpecifiers(){
        if(getInnerMessageType() != MessageType.SQUAD_INIT){
            return null;
        }
        return specifiers;
    }

    /**
     * Get the current hero whose turn it is
     */
    public String getCurrentHero(String hero){
        return payloadString;
    }

    /**
     * Gets the targeting data for a given hero
     */
    public HeroTarget getTargetData(){
        if(!targetable){
            return null;
        }
        return target;
    }

    /**
     * Gets the damage to a given hero
     */
    public Double getDamage(){
        if(innerMessageType != MessageType.HERO_ATTACK){
            return null;
        }
        return payloadDouble;
    }
}
