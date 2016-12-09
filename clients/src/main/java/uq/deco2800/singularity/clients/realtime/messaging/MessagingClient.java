package uq.deco2800.singularity.clients.realtime.messaging;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;

import uq.deco2800.singularity.clients.realtime.RealTimeClient;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.realtime.IncomingMessage;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;
import uq.deco2800.singularity.common.representations.realtime.StatusUpdate;
import uq.deco2800.singularity.common.util.StatusUpdateCodes;

/**
 * @author dloetscher
 *		
 */
public class MessagingClient extends RealTimeClient {
	
	private static final String CLASS = MessagingClient.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);
	
	private MessagingListener messagingListener = new MessagingListener();
	private boolean willReceiveMessages = false;
	private SessionType gameType;
	
	/**
	 * @param configuration
	 * @param client
	 * @param gameType
	 * @throws IOException
	 */
	public MessagingClient(RealTimeSessionConfiguration configuration, SingularityRestClient client,
			SessionType gameType) throws IOException {
		super(configuration, client);
		realTimeClient.addListener(messagingListener);
		this.gameType = gameType;
		register();
	}
	
	/**
	 * @param listener
	 */
	public void addListener(MessagingEventListener listener) {
		messagingListener.addListener(listener);
	}
	
	/**
	 * @param listener
	 */
	public void removeListener(MessagingEventListener listener) {
		messagingListener.removeListener(listener);
	}
	
	/**
	 * @param destinationThreadId
	 * @param message
	 */
	public void sendMessage(String destinationThreadId, String message) {
		if (!willReceiveMessages) {
			LOGGER.warn("Registration has not been successful - User may not receive replies");
		}
		Token token = restClient.renewIfNeededAndGetToken();
		IncomingMessage messageToOthers = new IncomingMessage();
		messageToOthers.setFromTokenId(token.getTokenId());
		messageToOthers.setFromUserId(token.getUserId());
		messageToOthers.setMessage(message);
		messageToOthers.setToThreadId(destinationThreadId);
		messageToOthers.setFromUserName(restClient.getUsername());
		LOGGER.info("Sending message: [{}]", messageToOthers);
		realTimeClient.sendTCP(messageToOthers);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * uq.deco2800.singularity.clients.realtime.RealTimeClient#getSessionType()
	 */
	@Override
	public SessionType getSessionType() {
		return gameType;
	}
	
	/**
	 * @author dloetscher
	 *		
	 */
	private class MessagingListener extends Listener {
		
		private List<MessagingEventListener> listeners;
		
		/**
		 * 
		 */
		public MessagingListener() {
			listeners = new LinkedList<>();
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * com.esotericsoftware.kryonet.Listener#received(com.esotericsoftware.
		 * kryonet.Connection, java.lang.Object)
		 */
		@Override
		public void received(Connection connection, Object object) {
			if (object instanceof IncomingMessage) {
				IncomingMessage message = (IncomingMessage) object;
				LOGGER.info("Received incoming message: [{}]", message);
				for (MessagingEventListener listener : listeners) {
					listener.didReceiveMessage(message);
				}
			} else if (object instanceof StatusUpdate) {
				StatusUpdate update = (StatusUpdate) object;
				LOGGER.info("Received status update: [{}]", update);
				if (update.getStatus() == StatusUpdateCodes.REGISTERED) {
					willReceiveMessages = true;
				}
			} else if (object instanceof KeepAlive) {
				LOGGER.trace("Received keep alive: [{}]", (KeepAlive) object);
			} else {
				LOGGER.info("Received unexpected object: [{}]", object);
			}
		}
		
		/**
		 * @param listener
		 */
		public void addListener(MessagingEventListener listener) {
			listeners.add(listener);
		}
		
		/**
		 * @param listener
		 */
		public void removeListener(MessagingEventListener listener) {
			listeners.remove(listener);
		}
	}
	
}
