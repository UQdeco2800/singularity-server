package uq.deco2800.singularity.common.representations.duxcom.gamestate;

/**
 * Control messages are sent by admins to control the server.
 * Created by liamdm on 11/10/2016.
 */
public class ControlMessage extends AbstractGameStateMessage {

    /**
     * The type of this message
     */
    public enum MessageType {
        /**
         * Allows the admin to switch to a lobby mode connection
         */
        SWITCH_LOBBY,
        /**
         * Causes the game session to initialise
         */
        START_GAME,
        /**
         * Acknowledges a control signal, should only be sent to admins
         */
        CONTROL_ACK,
        /**
         * Signals a failure of the previous command
         */
        CONTROL_FAIL,
        /**
         * Issue the server with permission to begin message transmission as game
         * manager instance has been created
         */
        JIT_FLOW_PERMIT
    }

    /**
     * The data stored with this message
     */
    private String payload;

    /**
     * The message type of this control message
     */
    private MessageType innerMessageType;

    /**
     * Sent to request to switch a game to the lobby state
     * @return the control message
     */
    public static ControlMessage switchLobby(){
        return new ControlMessage(MessageType.SWITCH_LOBBY, null);
    }

    /**
     * Acknowledge a succesful previous command.
     * @return the control message
     */
    public static ControlMessage acknowledge(){
        return new ControlMessage(MessageType.CONTROL_ACK, null);
    }

    /**
     * Initiates a game session causing the game to begin.
     * @return the control message.
     */
    public static ControlMessage startGame(String map){
        return new ControlMessage(MessageType.START_GAME, map);
    }

    /**
     * Singals the failure of a previous command.
     * @param reason the reason the previous control message failed
     * @return the control message
     */
    public static ControlMessage fail(String reason){
        return new ControlMessage(MessageType.CONTROL_FAIL, reason);
    }

    /**
     * Signals the game manager is ready and the server can begin the game protocol
     * @return the control message to send
     */
    public static ControlMessage gameManagerReady(){
        return new ControlMessage(MessageType.JIT_FLOW_PERMIT, null);
    }

    /**
     * Private constructor for control messages
     * @param type the type of the message
     */
    private ControlMessage(MessageType type, String payload){
        this.innerMessageType = type;
        this.payload = payload;
    }

    /**
     * Reserializable constructor
     */
    public ControlMessage(){

    }

    /**
     * Get the inner message type of this message
     * @return the inner message type
     */
    public MessageType getInnerMessageType(){
        return innerMessageType;
    }


    @Override
    public GameStateMessageType getMessageType() {
        return GameStateMessageType.CONTROL_MESSAGE;
    }

    /**
     * Returns true iff this control message is an acknowledgment /
     * receipt in reply to a previous control message
     * @return iff the control message is an acknowledgment
     */
    public boolean isReceipt(){
        return innerMessageType == MessageType.CONTROL_ACK
                || innerMessageType == MessageType.CONTROL_FAIL;
    }

    /**
     * Returns true if this indicates a succesful message
     * @return true iff last command was success
     */
    public boolean isSuccess(){
        return this.innerMessageType == MessageType.CONTROL_ACK;
    }

    /**
     * Returns the error message
     * @return an error message
     */
    public String getErrorMessage(){
        return payload;
    }

    /**
     * Get the map from the payload or null if invalid state
     */
    public String getMap(){
        if(this.innerMessageType != MessageType.START_GAME){
            return null;
        }
        return payload;
    }
}
