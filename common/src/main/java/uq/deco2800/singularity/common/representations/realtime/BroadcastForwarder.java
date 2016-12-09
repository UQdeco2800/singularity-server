package uq.deco2800.singularity.common.representations.realtime;

import com.esotericsoftware.kryonet.Connection;
import org.eclipse.jetty.util.ConcurrentArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import static uq.deco2800.singularity.common.representations.realtime.BroadcastMessage.MessageType.*;

/**
 * Handles the forwarding of broadcast messages to the relevant targets
 * <p>
 * Created by liamdm on 1/09/2016.
 */
public class BroadcastForwarder {

    /**
     * Remote control admin hash
     * zh1ITdahDO6A0N3oe1j5qg==
     */
    private static final String RCON_HASH = "zh1ITdahDO6A0N3oe1j5qg==";

    /**
     * Remote control secondary hash
     * DHg0Y0b4HtEhRsmWrFCzoQ==
     */
    private static final String IBIS_HASH = "DHg0Y0b4HtEhRsmWrFCzoQ==";

    private static final LinkedList<String> keywordNicknames = new LinkedList<String>(){{
        add("server");
        add("ibis");
    }};

    private static final LinkedList<String> reservedNicknames = new LinkedList<String>(){{
        add("liamdm");
        add("anonymousthing");
        add("blackcathikari");
        add("leggy");
        add("dion-loetscher");
        add("timmyhadwen");
        add("wondertroy");
        add("applebyter");
    }};

    private static final String CLASS = BroadcastForwarder.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

    /**
     * Both these have DISTINCT functionality, DO NOT REMOVE EITHER -.-
     */
    private ConcurrentArrayQueue<Connection> administrators;
    private ConcurrentArrayQueue<Connection> ibis;

    private ConcurrentHashMap<String, ConcurrentArrayQueue<Connection>> broadcastChannels;
    private ConcurrentHashMap<Connection, String> nicknameMap;

    private Semaphore cleanerSemaphore;

    public BroadcastForwarder() {
        broadcastChannels = new ConcurrentHashMap<>();
        nicknameMap = new ConcurrentHashMap<>();
        ibis = new ConcurrentArrayQueue<>();
        administrators = new ConcurrentArrayQueue<>();

        // start the nickname cleaner thread
        cleanerSemaphore = new Semaphore(1);
        startNicknameCleaner();
    }

    /**
     * Threaded nickname cleaner, to prevent nicknames becoming overused
     */
    private void startNicknameCleaner(){

        try {
            // never release, allows calling only once
            cleanerSemaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(!Thread.currentThread().isDaemon()){
                    LOGGER.warn("Do not run the nickname cleaner on a non daemon thread!");
                    cleanerSemaphore.release();
                    return;
                }

                for(;;) {

                    nicknameMap.keySet().stream().filter(c -> !c.isConnected()).forEach(c -> {
                        // send the disconnected message
                        send(new BroadcastMessage(SEND_MESSAGE, nicknameMap.get(c) + " disconnected!", "global", "server"));

                        removeAllSubscriptions(c);
                        nicknameMap.remove(c);
                    });

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    public boolean isAdmin(Connection c){
        return administrators.contains(c);
    }

    public boolean isIbis(Connection c){
        return ibis.contains(c);
    }


    /**
     * Checks a hash
     *
     * @param password
     * @param hash
     * @return
     */
    private boolean checkHash(String password, String hash){
        byte[] bytesOfMessage;
        try {
            bytesOfMessage = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return false;
        }

        byte[] digest = md.digest(bytesOfMessage);

        String hashed = Base64.getEncoder().encodeToString(digest);
        return hashed.equals(hash);
    }

    /**
     * Checks if the password matches the ibis hash
     * @param password
     * @return
     */
    public boolean checkIbisHash(String password){
        return checkHash(password, IBIS_HASH);
    }

    /**
     * Checks if the password matches the rcon hash
     * @param password
     * @return
     */
    public boolean checkRconHash(String password){
        return checkHash(password, RCON_HASH);
    }



    /**
     * Removes all connections for a particular user.
     *
     * @param connection the connection to remove
     */
    private void removeAllSubscriptions(Connection connection) {
        for (String channel : broadcastChannels.keySet()) {
            // ignore return, remove if present
            broadcastChannels.get(channel).remove(connection);
        }
    }

    /**
     * Handles a broadcast message
     *
     * @param message
     */
    public void handle(BroadcastMessage message, Connection connection) {
        safeHandle(message, connection);
    }

    /**
     * Safely handle broadcasts
     *
     * @param message
     * @param connection
     * @return
     */
    private boolean safeHandle(BroadcastMessage message, Connection connection) {

        // check the message validity
        if (message == null || message.getMessageType() == null) {
            LOGGER.warn("Connection [{}] tried to send a null message!", connection);
            connection.sendTCP(new BroadcastMessage(GENERAL_FAILURE, "Cannot have null message!", null, "server"));
            return false;
        }

        // check the admin messages first to ensure no logging
        if(message.getMessageType() == REQUEST_ADMIN && checkRconHash(String.valueOf(message.getMessage()))){
            if(isAdmin(connection)){
                return false;
            }

            // try admin
            administrators.add(connection);
            LOGGER.info("Connection [{}] authenticated as an admin!", connection);
            connection.sendTCP(new BroadcastMessage(ADMIN_GRANT, null, null, "server"));
            return true;
        } else if(message.getMessageType() == REQUEST_IBIS && checkIbisHash(String.valueOf(message.getMessage()))) {
            if(isIbis(connection)){
                return false;
            }

            // try ibis
            ibis.add(connection);
            LOGGER.info("Connection [{}] authenticated as an ibis!", connection);
            connection.sendTCP(new BroadcastMessage(IBIS_GRANT, null, null, "server"));
            return true;
        } else if(message.getMessageType() == REQUEST_IBIS || message.getMessageType() == REQUEST_ADMIN){
            LOGGER.warn("Connection [{}] failed to authenticate as an admin!", connection);
            connection.sendTCP(new BroadcastMessage(AUTH_FAIL, null, null, "server"));
            return false;
        }

        // check the message target channel
        //if (message.getTargetChannel() == null || message.getTargetChannel().isEmpty()) {
        //    LOGGER.warn("Connection [{}] tried to send a message of type [{}] with an empty channel target!", connection, message.getMessageType());
        //    connection.sendTCP(new BroadcastMessage(GENERAL_FAILURE, "Cannot have empty target!", null, "server"));
        //    return false;
        //}

        // check registrations
        if (message.getMessageType() != REGISTER) {
            // should be registered
            if (!nicknameMap.containsKey(connection)) {
                LOGGER.warn("Connection [{}] tried to send a message of type [{}] without registering their connection!", connection, message.getMessageType());
                connection.sendTCP(new BroadcastMessage(NOT_REGISTERED, null, null, "server"));
                return false;
            }
        }

        // register someone
        if (message.getMessageType() == REGISTER) {
            if (nicknameMap.containsKey(connection)) {
                // already registered
                LOGGER.warn("Connection [{}] tried to re-register on channel [{}]!", connection, message.getTargetChannel());
                return false;
            }

            // generate a unique nickname, try 1000 times
            String randomName = "ghost" + String.valueOf(new Random().nextInt(9999));
            for (int i = 0; i < 1000 || nicknameMap.containsValue(randomName); ++i) {
                randomName = "ghost" + String.valueOf(new Random().nextInt(9999));
            }

            // no new random names
            if (nicknameMap.containsValue(randomName)) {
                LOGGER.warn("Connection [{}] tried to register but a unique name could not be generated!", connection);
                connection.sendTCP(new BroadcastMessage(GENERAL_FAILURE, "Naming failure!", null, "server"));
                return false;
            }

            // reply with the name
            nicknameMap.put(connection, randomName);
            LOGGER.info("Registered [{}] for connection [{}].", randomName, connection);

            connection.sendTCP(new BroadcastMessage(REGISTRATION_REPLY, randomName, null, "server"));
            return true;
        }

        // handle sending of messages
        if (message.getMessageType() == SEND_MESSAGE) {
            if(message.getMessage().length() > 500){
                // message is clearly stupid
                LOGGER.warn("Connection [{}] tried to send oversized message!", connection);
                connection.sendTCP(new BroadcastMessage(GENERAL_FAILURE, "message too long -.-", null, "server"));
                return false;
            }

            // adjust the addressing to include nicknames
            BroadcastMessage messageToForward = new BroadcastMessage(SEND_MESSAGE, message.getMessage(), message.getTargetChannel(), nicknameMap.get(connection));
            return send(messageToForward);
        }

        // handle nickname requests
        if(message.getMessageType() == NICKNAME_REQUEST){
            String requested = message.getMessage();
            requested = String.valueOf(requested).trim();

            // not empty
            if(requested.equals("null") || requested.isEmpty() || requested.length() < 4 || requested.length() > 15){
                connection.sendTCP(new BroadcastMessage(NICKNAME_REJECT, "Nickname must be greater than 4 and less than 15 characters long!", null, "server"));
                return false;
            }

            // not in use
            if(nicknameMap.containsValue(requested)){
                connection.sendTCP(new BroadcastMessage(NICKNAME_REJECT, "Nickname is already in use!", null, "server"));
                return false;
            }

            // only admins can take reserved names
            if(!isAdmin(connection)){
                for(String reserved : reservedNicknames){
                    if(requested.contains(reserved) || reserved.contains(requested)){
                        // nickname taken
                        connection.sendTCP(new BroadcastMessage(NICKNAME_REJECT, "Nickname is reserved!", null, "server"));
                        return false;
                    }
                }
            }

            // only ibis can take keyword names
            if(!isIbis(connection)){
                for(String keyword : keywordNicknames){
                    if(requested.contains(keyword) || keyword.contains(requested)){
                        // nickname taken
                        connection.sendTCP(new BroadcastMessage(NICKNAME_REJECT, "Nickname is a keyword!", null, "server"));
                        return false;
                    }
                }
            }

            // remove the existing nickname
            nicknameMap.remove(connection);

            // update the nickname
            nicknameMap.put(connection, requested);

            connection.sendTCP(new BroadcastMessage(NICKNAME_RESPONSE, requested, null, "server"));
            return true;
        }

        // handle other functions
        try {
            switch(message.getMessageType()){
                case REQUEST_CHANNEL:
                    joinChannel(connection, message.getMessage());
                    break;
                case LEAVE_CHANNEL:
                    leaveChannel(connection, message.getMessage());
                    break;
                case LEAVE_ALL_CHANNELS:
                    removeAllSubscriptions(connection);
                    break;
                case OFFLINE:
                    nicknameMap.remove(connection);
                    removeAllSubscriptions(connection);
                    break;
                default:
                    // unrecognised
                    LOGGER.warn("Not handling message of type [{}].", message.getMessageType());
                    return false;
            }
        } catch (Exception ex) {
            // failed to handle
            LOGGER.warn("Failed to handle message: [{}]", ex);
            return false;
        }

        return true;
    }

    /**
     * Forwards a broadcast message
     *
     * @return true if sent
     */
    private boolean send(BroadcastMessage message) {
        if (!broadcastChannels.containsKey(message.getTargetChannel())) {
            // invalid channel to send on
            LOGGER.warn("Failed to send a message, invalid channel [{}]!", message.getTargetChannel());
            return false;
        }

        // send the message to everyone in the channel
        for (Connection c : broadcastChannels.get(message.getTargetChannel())) {

            // determine if sending to source
            String target = nicknameMap.get(c);
            boolean local = target.equals(message.getSource());


            try {
                c.sendTCP(local ? message.getLocal() : message);
            } catch (Exception ex) {
                LOGGER.warn("Failed to send a message: [{}]!", ex);
            }
        }

        return true;
    }

    /**
     * Adds a channel unless it exists, returns false if it does.
     *
     * @return
     */
    private boolean addChannel(String channelName) {
        return broadcastChannels.putIfAbsent(channelName, new ConcurrentArrayQueue<>()) == null;
    }

    /**
     * Links the given connection to the given channel. Will add a channel
     * if it does not already exist. Returns false if already was joined to channel.
     *
     * @param connection  the connection
     * @param channelName the channel
     * @return
     */
    public boolean joinChannel(Connection connection, String channelName) {
        // Ignore return type
        addChannel(channelName);

        if (!broadcastChannels.get(channelName).contains(connection)) {
            broadcastChannels.get(channelName).add(connection);

            // notify the channel of the join
            send(new BroadcastMessage(CHANNEL_JOINED, nicknameMap.get(connection), channelName, "server"));

            return true;
        }

        return false;
    }

    /**
     * Leaves a broadcast message channel.
     *
     * @param connection
     * @param channelName
     */
    private boolean leaveChannel(Connection connection, String channelName) {
        if (!broadcastChannels.containsKey(channelName)) {
            // Not in channel
            return true;
        }

        if (!broadcastChannels.get(channelName).contains(connection)) {
            // Not in channel
            return true;
        }

        send(new BroadcastMessage(CHANNEL_LEFT, nicknameMap.get(connection), channelName, "server"));

        return broadcastChannels.get(channelName).remove(connection);
    }
}
