package uq.deco2800.singularity.server.messaging;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;

import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.MessageChannel;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.realtime.*;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.GameStateMessage;
import uq.deco2800.singularity.common.util.StatusUpdateCodes;
import uq.deco2800.singularity.server.authentication.TokenDao;

public class MessagingListener extends Listener {
	
	private static final String CLASS = MessagingListener.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);
	
	private MessagingChannelDao messageChannelDao;
	private TokenDao tokenDao;
	private ConcurrentHashMap<String, Connection> userToConnectionMap;
	private ConcurrentHashMap<Connection, String> connectionToUserMap;
	
	public MessagingListener(MessagingChannelDao messageChannelDao, TokenDao tokenDao) {
		super();
		userToConnectionMap = new ConcurrentHashMap<>();
		connectionToUserMap = new ConcurrentHashMap<>();
		this.messageChannelDao = messageChannelDao;
		this.tokenDao = tokenDao;
	}


	private BroadcastForwarder broadcastForwarder = new BroadcastForwarder();

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof Registration) {
			Registration registration = (Registration) object;
			LOGGER.info("Received a registration [{}] request from [{}]", registration, connection);

			// registration is for broadcast
			if (handleRegistrationAsBroadcast(registration, connection)) {
				return;
			}

			StatusUpdate statusUpdate = validateToken(registration.getTokenId(), registration.getUserId());
			if (statusUpdate.getStatus() >= 400) { // 400 is start of bad errors
				connection.sendTCP(statusUpdate);
				return;
			}
			connectionToUserMap.put(connection, registration.getUserId());
			userToConnectionMap.put(registration.getUserId(), connection);
			connection.sendTCP(new StatusUpdate(StatusUpdateCodes.REGISTERED, "registration successful"));
			return;
		} else if (object instanceof IncomingMessage) {
			IncomingMessage message = (IncomingMessage) object;
			LOGGER.info("Received an incoming message [{}] from [{}]", message, connection);
			StatusUpdate statusUpdate = validateToken(message.getFromTokenId(), message.getFromUserId());
			if (statusUpdate.getStatus() >= 400) { // 400 is start of bad errors
				connection.sendTCP(statusUpdate);
				return;
			}
			if (message.getToMessageChannelId() == null) {
				connection.sendTCP(new StatusUpdate(StatusUpdateCodes.BAD_REQUEST, "Destination channel needed"));
				return;
			}
			if (messageChannelDao.getUsersChannelRecord(message.getToMessageChannelId(),
					message.getFromUserId()) == null) {
				connection.sendTCP(
						new StatusUpdate(StatusUpdateCodes.NOT_FOUND, "User is not a participant of this channel"));
				return;
			}
			List<MessageChannel> destinationChannels = messageChannelDao
					.getMessageChannelsByChannelId(message.getToMessageChannelId());
			message.setFromTokenId(null); // don't forward token to different
										  // recipients
			for (int i = 0; i < destinationChannels.size(); ++i) {
				MessageChannel destinationChannel = destinationChannels.get(i);
				String destinationUserId = destinationChannel.getUserId();
				Connection destination = userToConnectionMap.get(destinationUserId);
				// It's ok to send back to user who sent. That's why it's not
				// checked
				if (destination != null) {
					synchronized (destination) {
						destination.sendTCP(message);
					}
				}
			}
		} else if (object instanceof KeepAlive) {
			LOGGER.trace("Received a KeepAlive: [{}]", (KeepAlive) object);
		} else if (object instanceof BroadcastMessage) {
			LOGGER.trace("Received a broadcast message: [{}]", (BroadcastMessage) object);
			BroadcastMessage bm = (BroadcastMessage) object;
			broadcastForwarder.handle(bm, connection);
		} else if (object instanceof GameStateMessage){
			LOGGER.trace("Recieved a game state message...");

		} else {
			LOGGER.warn("Received unexpected object [{}] from connection [{}]", object, connection);
			connection.sendTCP(new StatusUpdate(StatusUpdateCodes.BAD_REQUEST,
					"Unexpected object received. Do you have the correct class registration order?"));
			return;
		}
	}

	private boolean handleRegistrationAsBroadcast(Registration registration, Connection connection) {
		if(!registration.getSession().equals(SessionType.BROADCAST)){
			return false;
		}

		broadcastForwarder.joinChannel(connection, "global");

		return true;
	}

	private StatusUpdate validateToken(String tokenId, String userId) {
		if (tokenId == null || userId == null) {
			return new StatusUpdate(StatusUpdateCodes.BAD_REQUEST, "A user id and token are required");
		}
		Token token = tokenDao.findByTokenId(tokenId);
		if (token == null || token.getExpires() <= System.currentTimeMillis() || !token.getUserId().equals(userId)) {
			return new StatusUpdate(StatusUpdateCodes.FORBIDDEN,
					"Token is invalid or has expired or token does not match user");
		}
		return new StatusUpdate(StatusUpdateCodes.OK, "Valid");
	}
	
	@Override
	public void connected(Connection connection) {
		super.connected(connection);
	}
	
	@Override
	public void disconnected(Connection connection) {
		String userId = connectionToUserMap.get(connection);
		LOGGER.info("User [{}] has disconnected - Clearing registration", userId);
		if (userId != null) {
			connectionToUserMap.remove(connection);
			userToConnectionMap.remove(userId);
		}
	}
}
