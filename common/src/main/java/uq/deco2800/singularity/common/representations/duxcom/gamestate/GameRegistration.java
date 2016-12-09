package uq.deco2800.singularity.common.representations.duxcom.gamestate;

import javax.ws.rs.WebApplicationException;

/**
 * Used to register as part of a game.
 *
 * Created by liamdm on 9/10/2016.
 */
public class GameRegistration extends AbstractGameStateMessage {

    /**
     * The reason for failure
     */
    private FailureReason failureReason;

    /**
     * The type of this game registration
     */
    private MessageType messageType = null;

    /**
     * The data with this message
     */
    private String payload = null;

    /**
     * The inner types of these messages
     */
    public enum MessageType {
        /**
         * Request a game session is hosted on the caller
         */
        REQUEST_CREATE,
        /**
         * Request to join a game session that is hosted
         */
        REQUEST_JOIN,
        /**
         * Response from server: success in previous request
         */
        REPLY_SUCCESS,
        /**
         * Response from server: failed previous request
         */
        REPLY_FAIL
    }

    /**
     * Reasons for failure in game registration
     */
    public enum FailureReason {
        /**
         * The game was in an invalid state and joining was not possible
         */
        INVALID_GAME_STATE
    }

    @Override
    public GameStateMessageType getMessageType() {
        return GameStateMessageType.GAME_REGISTRATION_MESSAGE;
    }

    /**
     * Gets the GameRegistration.MessageType type of this message.
     * @return the message type
     */
    public MessageType getInnerMessageType(){
        return messageType;
    }


    /**
     * Requests a session to be created.
     * @param username the username of the user to register
     * @return the game registration message to send
     */
    public static GameRegistration requestSession(String username){
        return new GameRegistration(MessageType.REQUEST_CREATE, username, null);
    }


    /**
     * Requests to join a session.
     * @param username the username of the user joining
     * @return the game registration needed to be sent to join
     */
    public static GameRegistration requestJoin(String username){
        return new GameRegistration(MessageType.REQUEST_JOIN, username, null);
    }

    /**
     * Generates a response indicating succesfull initiation
     * @return a game registration message
     */
    public static GameRegistration success(){
        return new GameRegistration(MessageType.REPLY_SUCCESS, null, null);
    }

    /**
     * Generates a response indicating a failure to initiate
     * @param error the error caused
     * @return a game registration message
     */
    public static GameRegistration failure(FailureReason failureReason, String error){
        return new GameRegistration(MessageType.REPLY_FAIL, error, failureReason);
    }


    /**
     * The game registration constructor
     * @param type the type of the message
     * @param data the data to send
     * @param reason optional, failure reason
     */
    private GameRegistration(MessageType type, String data, FailureReason reason){
        this.messageType = type;
        this.payload = data;
        if(reason != null){
            this.failureReason = reason;
        }
    }


    /**
     * Null constructor for deserialising
     */
    public GameRegistration(){

    }

    /**
     * Get the username of a user sending a request
     * @return the name of the user
     */
    public String getRequesterUsername(){
        if(this.getInnerMessageType() != MessageType.REQUEST_CREATE
                && this.getInnerMessageType() != MessageType.REQUEST_JOIN){
            throw new WebApplicationException("Failed to get username of the requester, this is not sent in a response message!");
        }

        return payload;
    }

    /**
     * Returns true if this message indicates the game session was initialised succesfully.
     * @return iff the game session was initialised succesfully
     */
    public boolean createdSuccesfully(){
        return this.getInnerMessageType() == MessageType.REPLY_SUCCESS;
    }

    /**
     * Returns true if this message is a creation request for creating a new server.
     * @return iff this is a creation request.
     */
    public boolean isCreationRequest(){
        return this.getInnerMessageType() == MessageType.REQUEST_CREATE;
    }

    /**
     * Returns true if this message is a request
     * @return iff the message is a request
     */
    public boolean isRequest(){
        return this.getInnerMessageType() == MessageType.REQUEST_JOIN
                || this.getInnerMessageType() == MessageType.REQUEST_CREATE;
    }

    /**
     * Returns the error message associated with this class if available.
     * @return the error message
     */
    public String getErrorMessage(){
        if(this.getInnerMessageType() != MessageType.REPLY_FAIL){
            throw new WebApplicationException("Failed to get error message, this is not a failure response!");
        }

        return payload;
    }

    public FailureReason getErrorCode(){
        return this.failureReason;
    }
}
