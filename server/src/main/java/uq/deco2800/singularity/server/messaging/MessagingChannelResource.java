package uq.deco2800.singularity.server.messaging;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.MessageChannel;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.server.authentication.TokenDao;

/**
 * @author dloetscher
 *
 */
@Path(ServerConstants.MESSAGE_CHANNEL_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class MessagingChannelResource {
	
	private MessagingChannelDao messageChannelDao;
	private TokenDao tokenDao;
	
	private static final String CLASS = MessagingChannelResource.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public MessagingChannelResource(MessagingChannelDao messageChannelDao, TokenDao tokenDao) {
		this.messageChannelDao = messageChannelDao;
		this.tokenDao = tokenDao;
	}
	
	/**
	 * @param tokenId
	 * @return
	 */
	@GET
	public Response getAllChannelsByToken(@QueryParam("token") Optional<String> tokenId) {
		if (!tokenId.isPresent()) {
			throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
		}
		Token token = tokenDao.findByTokenId(tokenId.get());
		if (token == null || token.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
		}
		List<MessageChannel> channels = messageChannelDao.getMessageChannelsByUserId(token.getUserId());
		return Response.ok(channels).build();
	}
	
	/**
	 * @param payload
	 * @param tokenId
	 * @return
	 */
	@POST
	@Path(ServerConstants.NEW)
	public Response createNewChannel(String payload, @QueryParam("token") Optional<String> tokenId) {
		LOGGER.info("Request to create new channel received: [{}] using token: [{}]", payload, tokenId);
		if (!tokenId.isPresent() || tokenId.get().isEmpty()) {
			throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
		}
		Token token = validateAndRetrieveToken(tokenId.get());
		List<MessageChannel> newChannels = null;
		try {
			newChannels = MAPPER.readValue(payload,
					MAPPER.getTypeFactory().constructCollectionType(List.class, MessageChannel.class));
			// Also need to set user who creates channel
			MessageChannel ownerChannel = new MessageChannel();
			ownerChannel.setUserId(token.getUserId());
			newChannels.add(ownerChannel);
		} catch (IOException e) {
			throw new WebApplicationException("Malformed request", Status.BAD_REQUEST);
		}
		String channelId = getExistingChannelIdFromParticipants(newChannels);
		if (channelId == null) {
			channelId = generateChannelId();
			for (MessageChannel channel : newChannels) {
				channel.setChannelId(channelId);
				messageChannelDao.insert(channel);
			}
		}
		return Response.ok(channelId).build();
	}
	/**
 	 * @param channel
 	 * @return
 	 */
 	@DELETE
 	@Path(ServerConstants.REMOVE)
 	public void removeSelectedChannel(@QueryParam("token") String tokenId, @QueryParam("channelId") String channelId) {
 		LOGGER.info("Attempting to remove selected channel channel remove: [{}] using token: [{}]", channelId,tokenId);
 		MessageChannel mChannel =new MessageChannel();
 		Token token = validateAndRetrieveToken(tokenId);
 		mChannel.setChannelId(channelId);
 		mChannel.setUserId(token.getUserId());;
 		messageChannelDao.remove(mChannel);
 	}
	/**
	 * @param requestedParticipants
	 * @return
	 */
	private String getExistingChannelIdFromParticipants(List<MessageChannel> requestedParticipants) {
		LOGGER.info("Attempting to bypass creating and return existing channel");
		// just need a user
		String userId = requestedParticipants.get(0).getUserId();
		List<MessageChannel> usersMessageChannels = messageChannelDao.getMessageChannelsByUserId(userId);
		for (MessageChannel channel : usersMessageChannels) {
			List<MessageChannel> channelParticipants = messageChannelDao
					.getMessageChannelsByChannelId(channel.getChannelId());
			if (channelParticipants.size() != requestedParticipants.size()) {
				continue;
			}
			HashSet<String> channelParticipantsIds = new HashSet<String>();
			for (MessageChannel participant : channelParticipants) {
				channelParticipantsIds.add(participant.getUserId());
			}
			boolean complete = true;
			for (MessageChannel requestedParticipant : requestedParticipants) {
				if (!channelParticipantsIds.contains(requestedParticipant)) {
					complete = false;
					break;
				}
			}
			if (complete) {
				LOGGER.info("Existing channel found.");
				return channel.getChannelId();
			}
		}
		LOGGER.info("Existing channel not found.");
		return null;
	}

	
	/**
	 * @param tokenId
	 * @param channelId
	 * @return
	 */
	@GET
	@Path(ServerConstants.MESSAGE_CHANNEL_PARTICIPANTS)
	public Response getParticipants(@QueryParam("token") Optional<String> tokenId,
			@QueryParam("channelId") Optional<String> channelId) {
		LOGGER.info("Received request to retrieve chat particpants with token [{}] for channel [{}]", tokenId,
				channelId);
		if (!tokenId.isPresent() || tokenId.get().isEmpty() || !channelId.isPresent() || channelId.get().isEmpty()) {
			throw new WebApplicationException("Request should contain a tokenId and a channelId as query parameters",
					Status.BAD_REQUEST);
		}
		Token token = validateAndRetrieveToken(tokenId.get());
		if (messageChannelDao.getUsersChannelRecord(channelId.get(), token.getUserId()) == null) {
			throw new WebApplicationException("User is not a participant of this channel", Status.NOT_FOUND);
		}
		List<MessageChannel> channels = messageChannelDao.getMessageChannelsByChannelId(channelId.get());
		boolean isParticipant = false;
		for (MessageChannel channel : channels) {
			if (channel.getUserId().equals(token.getUserId())) {
				isParticipant = true;
				break;
			}
		}
		if (isParticipant) {
			return Response.ok(channels).build();
		} else {
			throw new WebApplicationException("The user does not have permission to get this channel's participants",
					Status.FORBIDDEN);
		}
	}
	
	/**
	 * @return
	 */
	private String generateChannelId() {
		String id = UUID.randomUUID().toString();
		while (!messageChannelDao.getMessageChannelsByChannelId(id).isEmpty()) {
			id = UUID.randomUUID().toString();
		}
		return id;
	}
	
	/**
	 * @param tokenId
	 * @return
	 */
	private Token validateAndRetrieveToken(String tokenId) {
		Token token = tokenDao.findByTokenId(tokenId);
		if (token == null || token.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
		}
		return token;
	}
	
}
