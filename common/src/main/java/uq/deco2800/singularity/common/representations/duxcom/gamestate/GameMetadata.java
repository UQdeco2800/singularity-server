package uq.deco2800.singularity.common.representations.duxcom.gamestate;

import java.util.LinkedList;
import java.util.List;

/**
 * Queries data regarding a game.
 *
 * Created by liamdm on 9/10/2016.
 */
public class GameMetadata extends AbstractGameStateMessage {

    /**
     * This is true if this message is a reply
     */
    private boolean isReply;

    @Override
    public GameStateMessageType getMessageType() {
        return GameStateMessageType.GAME_METADATA_QUERY;
    }

    /**
     * Stores a payload register that can contain multiple peices of information
     */
    private List<String> metadata;

    /**
     * Returns true if this is a reply message
     * @return iff this is a reply message
     */
    public boolean isReply() {
        return isReply;
    }

    /**
     * The type of this message
     */
    public enum MessageType {
        /**
         * User data was just updated
         */
        USER_DATA
    }

    /**
     * The message type of this message
     */
    private MessageType messageType;

    /**
     * Request an update of the user data metadata
     * @return the game metadata message
     */
    public static GameMetadata requestJoinedUpdate(){
        return new GameMetadata(MessageType.USER_DATA, null);
    }

    /**
     * Creates a user data update reply with the users that have joined
     * @param joined the users joined
     * @return the game metadata message
     */
    public static GameMetadata userDataUpdate(List<String> joined){
        return new GameMetadata(MessageType.USER_DATA, new LinkedList<String>(joined));
    }

    /**
     * Private constructor for game metadata
     * @param type the type of the message
     * @param metadata the metadata to send
     */
    private GameMetadata(MessageType type, List<String> metadata){
        this.messageType = type;
        if(metadata == null){
            isReply = false;
        } else {
            this.metadata = metadata;
        }
    }

    /**
     * Get the list of joined users .
     * @return the list of joined users
     */
    public List<String> getJoinedUsers(){
        return metadata;
    }

    /**
     * Gets the inner type of this message
     * @return the inner type of this message
     */
    public MessageType getInnerMessageType(){
        return messageType;
    }

    /**
     * Deserializer constructor
     */
    public GameMetadata(){

    }


}
