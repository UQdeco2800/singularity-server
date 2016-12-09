package uq.deco2800.singularity.common.representations.realtime;

//import javafx.scene.paint.Color;

import java.util.UUID;

import static uq.deco2800.singularity.common.representations.realtime.BroadcastMessage.MessageType.*;

/**
 * A broadcast message
 *
 * Created by liamdm on 1/09/2016.
 */
public class BroadcastMessage {

    /**
     * If you sent the message
     * @return
     */
    public boolean isLocal() {
        return isLocal;
    }

    /**
     * Sets the is local prameter, denoting if you sent the message
     */
    public BroadcastMessage getLocal() {
        BroadcastMessage bm = new BroadcastMessage(messageType, message, targetChannel, source);
        bm.isLocal = true;
        return bm;
    }

    public enum MessageType {
        // Requesta channel
        REQUEST_CHANNEL,
        // Channel status messages
        CHANNEL_JOINED, CHANNEL_LEFT,
        // Send a message
        SEND_MESSAGE,
        // Leave channels
        LEAVE_ALL_CHANNELS, LEAVE_CHANNEL,
        // Request your nickname
        NICKNAME_REQUEST, NICKNAME_RESPONSE, NICKNAME_REJECT,
        // Register yourself on joining a channel to recieve a new nickname
        REGISTER, REGISTRATION_REPLY,
        // Returned if you try to send messages without registering
        NOT_REGISTERED,
        // Something went wrong
        GENERAL_FAILURE,
        // Request admin status
        REQUEST_ADMIN, REQUEST_IBIS,
        // Responses to admin status requests
        ADMIN_GRANT, IBIS_GRANT, AUTH_FAIL,
        // Go offline
        OFFLINE
    }

    private MessageType messageType;
    private String targetChannel;
    private String message;
    private String source;
    private boolean isLocal = false;


    /**
     * Get the sender of the message
     * @return
     */
    public String getSource(){
        return source;
    }

    public static BroadcastMessage getNickname(String nickname){
        return new BroadcastMessage(NICKNAME_REQUEST, nickname, null, null);
    }

    public static BroadcastMessage goOffline(){
        return new BroadcastMessage(OFFLINE, null, null, null);
    }

    public static BroadcastMessage login(String key, boolean ibis){
        return new BroadcastMessage(ibis ? REQUEST_IBIS : REQUEST_ADMIN, key, null, null);
    }

    public static BroadcastMessage register(){
        return new BroadcastMessage(REGISTER, null, null, null);
    }

    /**
     * Creates a leave all channels message
     * @return
     */
    public static BroadcastMessage leaveAllChannels(){
        return new BroadcastMessage(LEAVE_ALL_CHANNELS, null, null, null);
    }

    /**
     * Creates a leave channel message
     * @param channelName
     * @return
     */
    public static BroadcastMessage leaveChannel(String channelName){
        return new BroadcastMessage(LEAVE_CHANNEL, channelName, null, null);
    }

    public static BroadcastMessage joinChannel(String channelName){
        return createChannel(channelName);
    }

    /**
     * Creates a create channel message
     * @param channelName the channel name to create
     * @return
     */
    public static BroadcastMessage createChannel(String channelName){
        return new BroadcastMessage(REQUEST_CHANNEL, channelName, null, null);
    }

    /**
     * Creates a broadcast message
     * @param message the message to send
     * @param target the targetChannel to send to
     * @return
     */
    public static BroadcastMessage createBroadcastMessage(String message, String target){
        return new BroadcastMessage(SEND_MESSAGE, message, target, null);
    }

    /**
     * Null constructor needed for re-serialization
     */
    public BroadcastMessage() {}

    public BroadcastMessage(MessageType messageType, String message, String targetChannel, String source) {
        this.messageType = messageType;
        this.message = message;
        this.targetChannel = targetChannel;
        this.source = source;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }

    public String getTargetChannel() {
        return targetChannel;
    }

    @Override
    public String toString() {
        return "[BM_"+messageType.toString()+" <SRC:" + String.valueOf(source) + "> <DST:" + String.valueOf(this.targetChannel) + "> <DATA:" + String.valueOf(String.valueOf(this.message).hashCode()) + ">]";
    }
}
