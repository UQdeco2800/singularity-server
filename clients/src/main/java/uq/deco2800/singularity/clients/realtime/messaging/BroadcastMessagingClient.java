package uq.deco2800.singularity.clients.realtime.messaging;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.clients.realtime.RealTimeClient;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.realtime.*;
import uq.deco2800.singularity.common.util.StatusUpdateCodes;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles broadcast messages.
 *
 * Created by liamdm on 21/09/2016.
 */
public class BroadcastMessagingClient extends RealTimeClient {

    private static final String CLASS = MessagingClient.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

    private BroadcastListener broadcastListener = new BroadcastListener();

    /**
     * The broadcast messaging client
     *
     * @param configuration
     * @param restClient
     * @throws IOException
     */
    public BroadcastMessagingClient(RealTimeSessionConfiguration configuration, SingularityRestClient restClient) throws IOException {
        super(configuration, restClient);
        realTimeClient.addListener(broadcastListener);
    }

    @Override
    public void register() {
        Registration registrationAttempt = new Registration();
        registrationAttempt.setSession(getSessionType());
        realTimeClient.sendTCP(registrationAttempt);
    }

    @Override
    public SessionType getSessionType() {
        return SessionType.BROADCAST;
    }


    /**
     * Sends the given broadcast message
     * @param message the message to send
     */
    public void sendMessage(BroadcastMessage message) {
        LOGGER.info("Sending message: [{}]", message);
        realTimeClient.sendTCP(message);
    }

    /**
     * @param listener
     */
    public void addListener(BroadcastMessagingEventListener listener) {
        broadcastListener.addListener(listener);
    }

    /**
     * @param listener
     */
    public void removeListener(BroadcastMessagingEventListener listener) {
        broadcastListener.removeListener(listener);
    }

    /**

    /**
     * @author dloetscher
     *
     */
    private class BroadcastListener extends Listener {

        private List<BroadcastMessagingEventListener> listeners;

        @Override
        public void disconnected(Connection connection) {
            LOGGER.info("Connection [{}] was dropped!", connection);
            for (BroadcastMessagingEventListener listener : listeners) {
                listener.disconnected();
            }
        }

        /**
         *
         */
        public BroadcastListener() {
            listeners = new LinkedList<>();
        }

        /*
         * Recieves a broadcast message
         */
        @Override
        public void received(Connection connection, Object object) {
            if (object instanceof BroadcastMessage) {
                BroadcastMessage message = (BroadcastMessage) object;
                LOGGER.info("Received incoming message: [{}]", message);
                for (BroadcastMessagingEventListener listener : listeners) {
                    listener.recievedBroadcastMessage(message);
                }
            } else {
                LOGGER.info("Received unexpected object: [{}]", object);
            }
        }

        /**
         * @param listener
         */
        public void addListener(BroadcastMessagingEventListener listener) {
            listeners.add(listener);
        }

        /**
         * @param listener
         */
        public void removeListener(BroadcastMessagingEventListener listener) {
            listeners.remove(listener);
        }
    }
}
