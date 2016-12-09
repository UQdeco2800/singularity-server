package uq.deco2800.singularity.common.representations.duxcom.gamestate;

import java.util.LinkedList;

/**
 * A player action message
 * <p>
 * Created by liamdm on 17/10/2016.
 */
public class PlayerAction extends AbstractGameStateMessage {

    /**
     * Used to send raw player messages
     * @param type the type of the message
     */
    public PlayerAction(MessageType type) {
        this.innerMessageType = type;
    }

    public boolean isTurnEnd() {
        return turnEndState == 1;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    /**
     * The message type of the player action message
     */
    public enum MessageType {
        /**
         * The player has moved
         */
        PLAYER_MOVE,

        /**
         * The player has ended their turn
         */
        END_TURN,

        /**
         * Player has left the game and is no longer
         * part of turn taking
         */
        PLAYER_LEFT,

        /**
         * A player has game over
         */
        PLAYER_GAME_OVER
    }

    /**
     * Sent when a players game is over
     */
    public static PlayerAction gameOver(){
        return new PlayerAction(MessageType.PLAYER_GAME_OVER);
    }

    /**
     * Sent when a player leaves the game
     */
    public static PlayerAction playerLeft(){
        return new PlayerAction(MessageType.PLAYER_LEFT);
    }


    /**
     * Point data for this class
     */
    private int x = -1;
    private int y = -1;

    /**
     * End turn data
     */
    private int turnEndState = -1;

    /**
     * The inner message type
     */
    private MessageType innerMessageType;

    /**
     * A players action on move
     */
    public static PlayerAction move(int x, int y) {
        return new PlayerAction(MessageType.PLAYER_MOVE, x, y, 0);
    }

    /**
     * A players turn end
     */
    public static PlayerAction endTurn() {
        return new PlayerAction(MessageType.END_TURN, -1, -1, 1);
    }

    /**
     * The private player action constructor
     *
     * @param x            optional x corrdinate
     * @param y            optional y coordinate
     * @param turnEndState turn end state
     */
    private PlayerAction(MessageType type, int x, int y, int turnEndState) {
        this.innerMessageType = type;
        this.x = x;
        this.y = y;
        this.turnEndState = turnEndState;
    }

    /*
     * the deserializer constructor for palyer actions
     */
    public PlayerAction() {

    }

    /**
     * Gets the inner message type
     */
    public MessageType getInnerMessageType() {
        return innerMessageType;
    }

    @Override
    public GameStateMessageType getMessageType() {
        return GameStateMessageType.PLAYER_ACTION;
    }
}
