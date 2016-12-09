package uq.deco2800.singularity.common.representations.duxcom.gamestate;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains data regarding an update in the playable state of a game.
 *
 * Created by liamdm on 16/10/2016.
 */
public class GameUpdate extends AbstractGameStateMessage{
    /**
     * The message type of the game update message
     */
    public enum MessageType {
        /**
         * Tells the client to initialse the map
         */
        MAP_INIT,
        /**
         * Tells the client to change players
         */
        PLAYER_CHANGE,
        /**
         * Tells the clients the order of players in game
         */
        PLAYER_ORDER,
        /**
         * The game seed used to unify everything
         */
        GAME_SEED,
        /**
         * A player is dead
         */
        PLAYER_DEAD
    }

    /**
     * The data stored with this class
     */
    private LinkedList<String> payload = new LinkedList<>();

    /**
     * The inner message type
     */
    private MessageType innerMessageType;

    public static GameUpdate notifyPlayerOrder(List<String> playerOrder){
        return new GameUpdate(MessageType.PLAYER_ORDER, playerOrder);
    }

    /**
     * Notify that there has been a player change. Theoretically does not enforce
     * the player change.
     */
    public static GameUpdate notifyPlayerChange(String newPlayer){
        return new GameUpdate(MessageType.PLAYER_CHANGE, newPlayer);
    }

    /**
     * Gets the message type of this message
     */
    public MessageType getInnerMessageType(){
        return this.innerMessageType;
    }

    /**
     * Creates an initialise map message with the maps initialisation vector
     * @param mapIV the map initialisation vector to use
     */
    public static GameUpdate initMap(String map, String mapIV){
        return new GameUpdate(MessageType.MAP_INIT, map, mapIV);
    }

    /**
     * Private constructor for the game update messages
     * @param messageType the type of the message
     * @param payload the payload to send
     */
    private GameUpdate(MessageType messageType, String ... payload){
        this.innerMessageType = messageType;
        Collections.addAll(this.payload, payload);
    }

    private GameUpdate(MessageType messageType, List<String> payload){
        this.innerMessageType = messageType;
        this.payload.addAll(payload);
    }

    /**
     * Deserializer constructor
     */
    public GameUpdate(){

    }

    /**
     * Returns the current player is this is a
     * PLAYER_CHANGE message otherwise null
     */
    public String getCurrentPlayer(){
        if(this.getInnerMessageType() != MessageType.PLAYER_CHANGE){
            return null;
        }
        return payload.get(0);
    }

    /**
     * Returns the player order or null on incorrect state
     */
    public List<String> getPlayerOrder(){
        if(this.getInnerMessageType() != MessageType.PLAYER_ORDER){
            return null;
        }
        return payload;
    }

    @Override
    public GameStateMessageType getMessageType() {
        return GameStateMessageType.GAME_UPDATE;
    }
}
