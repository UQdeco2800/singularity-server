package uq.deco2800.singularity.clients.restful;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.util.ConcurrentArrayQueue;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.MessageChannel;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.User;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * The main RESTful Client used to interact with the Singularity Client. All clients used for specific projects should
 * probably extend this client so that functionality specific to one game isn't available in another game's client.
 *
 * @author dion-loetscher
 */
public class SingularityRestClient {

	/**
	 * Do the multiplayer session config loading and cached functions to ensure
	 * multiplayer sessions are mapped correctly;
	 */
	private void doMSCCacheFunctions() {
		List<RealTimeSessionConfiguration> realTimeSessionConfigurations;
		try {
			realTimeSessionConfigurations = getActiveGames();
		} catch (IOException e) {
			// failed to cache
			LOGGER.error("Failed to cache the multiplayer sessions! Error: " + String.valueOf(e));
			return;
		}

		sessionIDMap.clear();

		for (RealTimeSessionConfiguration realTimeSessionConfiguration : realTimeSessionConfigurations) {
			if (isSessionNamed(realTimeSessionConfiguration)) {
				// MSC supported
				sessionIDMap.put(realTimeSessionConfiguration.getSessionID(), realTimeSessionConfiguration);
			}
		}
	}

	/**
	 * Stores a map between session ID and the real time sessions present
	 */
	private HashMap<String, RealTimeSessionConfiguration> sessionIDMap = new HashMap<>();

	/**
	 * Prevents duplicate quick connect ID's
	 */
	private Semaphore quickConnectSemaphore = new Semaphore(1);

	private static final String CLASS = SingularityRestClient.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	protected Client client;
	private int port;
	private String host;
	private Token token;
	private String username;

	/**
	 * Constructs a new client with the default settings given by
	 * {@link uq.deco2800.singularity.common.ServerConstants#PRODUCTION_SERVER ServerConstants.PRODUCTION_SERVER} and
	 * {@link uq.deco2800.singularity.common.ServerConstants#REST_PORT ServerConstants.REST_PORT}
	 */
	public SingularityRestClient() {
		this(ServerConstants.PRODUCTION_SERVER, ServerConstants.REST_PORT);
	}

	/**
	 * Initialises a new client which uses the given host and port to send requests.
	 *
	 * @param host A host like "singularity.rubberducky.io". Note that it does not include the protocol used like "http".
	 * @param port The port the destination is used to connect.
	 */
	public SingularityRestClient(String host, int port) {
		LOGGER.info("Initialising with  host: [{}] and port: [{}]", host, port);
		this.host = host == null || host.isEmpty() ? ServerConstants.LOCAL_HOST : host;
		this.port = port;
		token = null;
		client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
	}

	/**
	 * Builds a URI to the root resource of SinglarityServer using the port used when this instance was constructed.
	 *
	 * @return a UriBuilder of the root resource <em>http://{host}:{port}/</em>
	 */
	protected UriBuilder rootUriBuilder() {
		return rootUriBuilder(port);
	}

	/**
	 * Builds a URI to the root resource of SinglarityServer using the given port used when this instance was
	 * constructed.
	 *
	 * @param port The main application port that the Singularity Server is running on.
	 * @return a UriBuilder of the root resource <em>http://{host}:{port}/</em>
	 */
	protected UriBuilder rootUriBuilder(int port) {
		return UriBuilder.fromPath(ServerConstants.HTTP_PROTOCOL + host + ":" + port);
	}

	/**
	 * Used to set up the credentials in this class. Will also save the username for later so that the username this
	 * instance is associated with can be retrieved.
	 *
	 * @param username The username of the user that will be logged in. This must not be null or the empty string.
	 * @param password The password of the user that will be logged in. This must not be null or the empty string.
	 */
	public void setupCredentials(String username, String password) {
		LOGGER.info("Attempting to set up token with username: [{}], password: [{}]", username, password);
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.AUTHENTICATION_RESOURCE).path(ServerConstants.NEW)
				.queryParam("username", username).queryParam("password", password);
		LOGGER.info("About to get token");
		Response response = client.target(uriBuilder).request().get();
		LOGGER.info("Token attempt tried " + response.getStatusInfo().getStatusCode());
		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			LOGGER.warn("Token get not successful", response.getStatusInfo().getReasonPhrase());
			throw new WebApplicationException(response);
		}
		LOGGER.info("Response reading");
		token = response.readEntity(Token.class);
		this.username = username;
		LOGGER.info("Received token: [{}]", token);
	}

	/**
	 * This will first check if the token needs to be renewed. It will be renewed if the token has less than 1 hour
	 * before it expires. It will then attempt to renew the user's token using the existing token in order to retrieve a
	 * new token to be used for future communication. This will then set the token that is stored within this instance
	 * in {@link #token} and then return the token.
	 *
	 * @return A currently valid token associated with the username in this instance.
	 * @throws WebApplicationException The token has expired and therefore cannot be used to retrieve another token.
	 * @throws NullPointerException    if {@link #setupCredentials(String, String)} has not been run, then there will not be an initial
	 *                                 token to renew with and thus a {@link NullPointerException} is thrown.
	 */
	public Token renewIfNeededAndGetToken() throws WebApplicationException {
		if (token == null) {
			throw new NullPointerException("Client did not have its token set up.");
		}
		if (token.getExpires() <= System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)) {
			// If token has actually expired, server will respond with FORBIDDEN
			// which will be handled. So don't need to check for expiry.
			LOGGER.info("Token needs to be renewed as there is less than an hour "
					+ "left before it expires. Attempting renewal...");
			UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.AUTHENTICATION_RESOURCE)
					.path(ServerConstants.RENEW).queryParam("token", token.getTokenId());
			Response response = client.target(uriBuilder).request().get();
			if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
				throw new WebApplicationException(response);
			}
			token = response.readEntity(Token.class);
			LOGGER.info("Successfully renewed token: [{}]", token);
			return token;
		}
		return token;
	}

	/**
	 * Will create a {@link MessageChannel} between this user and all given user ids. If a user channel already exists
	 * with exactly the participants provided, will simply use the existing channel.
	 *
	 * @require This instance must have a valid token set up via either {@link #setupCredentials(String, String)} (if
	 *          this is the first time this instance is to be used) or {@link #renewIfNeededAndGetToken()} to check that
	 *          the token is valid and to renew if required.
	 * @param userIds
	 *            The userIds of the users to be added into one message channel.
	 * @return The channel ID (a UUID in String form) of an existing channel that exactly match the given participants,
	 *         else a new channel.
	 * @throws JsonProcessingException
	 *             Thrown if there was an error parsing the response into a POJO.
	 * @throws WebApplicationException
	 *             Thrown if there was an issue with the request (e.g. invalid token). Look at
	 *             {@link Response#getEntity()} and {@link Response#getStatusInfo()}, which is encapsulated in the
	 *             exception, for more information.
	 */
	/**
	 * @param userIds
	 * @return
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	public String createMessageChannel(String... userIds) throws JsonProcessingException, WebApplicationException {
		LOGGER.info("Attempting to create channels between current user and the following userIds: [{}]",
				Arrays.toString(userIds));
		renewIfNeededAndGetToken();
		List<MessageChannel> channels = new ArrayList<>(userIds.length);
		for (String userId : userIds) {
			MessageChannel channel = new MessageChannel();
			channel.setUserId(userId);
			channels.add(channel);
		}
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.MESSAGE_CHANNEL_RESOURCE)
				.path(ServerConstants.NEW).queryParam("token", token.getTokenId());
		String data = MAPPER.writeValueAsString(channels);
		Response response = client.target(uriBuilder).request().post(Entity.json(data));
		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		String channelId = response.readEntity(String.class);
		try {
			UUID.fromString(channelId);
			LOGGER.info("Successfully created channels");
		} catch (IllegalArgumentException exception) {
			LOGGER.error("The channel ID: [{}] is malformed. It should be a UUID", channelId);
			channelId = null;
		}
		return channelId;
	}

	/**
	 * Retrieves all the public user information given a username. This does not require that
	 * {@link #setupCredentials(String, String)} has been run as it does not require a token.
	 *
	 * @param username The username of the user to look up.
	 * @return The user that is being searched for.
	 * @throws WebApplicationException Thrown for a number of reasons (including User not found). Look at {@link Response#getEntity()} and
	 *                                 {@link Response#getStatusInfo()}, which is encapsulated in the exception, for more information.
	 */
	public User getUserInformationByUserName(String username) throws WebApplicationException {
		LOGGER.info("Attempting to retrieve information about: [{}]", username);
		// No need to renew token as authentication is not needed for this
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.USER_RESOURCE).queryParam("username", username);
		Response response = client.target(uriBuilder).request().get();
		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		User user = response.readEntity(User.class);
		LOGGER.info("Successfully retrieved information: [{}]", user);
		return user;
	}

	/**
	 * Retrieves all the public user information given a user ID (String representation of a {@link UUID}). This does
	 * not require that {@link #setupCredentials(String, String)} has been run as it does not require a token.
	 *
	 * @param userId The user ID (String based {@link UUID} of the user to look up. Must not be null or an empty string.
	 * @return The user that is being searched for.
	 * @throws WebApplicationException Thrown for a number of reasons (including User not found). Look at {@link Response#getEntity()} and
	 *                                 {@link Response#getStatusInfo()}, which is encapsulated in the exception, for more information.
	 */
	public User getUserInformationById(String userId) throws WebApplicationException {
		LOGGER.info("Attempting to retrieve information about: [{}]", userId);
		// No need to renew token as authentication is not needed for this
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.USER_RESOURCE).queryParam("userId", userId);
		Response response = client.target(uriBuilder).request().get();
		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		User user = response.readEntity(User.class);
		LOGGER.info("Successfully retrieved information: [{}]", user);
		return user;
	}

	/**
	 * Registers a new user on the server. This will then assign a UUID to the user and return the same user instance.
	 * This does not require that {@link #setupCredentials(String, String)} has been run as it does not require a token.
	 *
	 * @param user An instance of a User with all the details filled out except for the ID as that is generated by the
	 *             server. The username should be unique, and wll be verified by the server.
	 * @return The User object that was registered to the server, with the ID modified to be the one set by the server.
	 * This object is the same object as the one passed in to the method.
	 * @throws WebApplicationException Thrown for a number of reasons (including username already exists). Look at
	 *                                 {@link Response#getEntity()} and {@link Response#getStatusInfo()}, which is encapsulated in the
	 *                                 exception, for more information.
	 */
	public User createUser(User user) throws JsonProcessingException, WebApplicationException {
		LOGGER.info("Attempting to create user: [{}]", user);
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.USER_RESOURCE).path(ServerConstants.NEW);
		String data = MAPPER.writeValueAsString(user);
		Response response = client.target(uriBuilder).request().post(Entity.json(data));
		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		String userId = response.readEntity(String.class);
		user.setUserId(userId);
		LOGGER.info("Successfully created user with id: [{}]", userId);
		return user;
	}

	public List<MessageChannel> getUsersChannels()
			throws JsonParseException, JsonMappingException, IOException, WebApplicationException {
		renewIfNeededAndGetToken();
		LOGGER.info("Retrieving all channels by using token: [{}]", token);
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.MESSAGE_CHANNEL_RESOURCE).queryParam("token",
				token.getTokenId());
		Response response = client.target(uriBuilder).request().get();
		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		String data = response.readEntity(String.class);
		List<MessageChannel> channels = MAPPER.readValue(data,
				MAPPER.getTypeFactory().constructCollectionType(List.class, MessageChannel.class));

		LOGGER.info("Received the following channels: [{}]", channels);
		return channels;
	}
	
	public void removeCurrentChannel(String channelId)throws JsonParseException, JsonMappingException, IOException, WebApplicationException {
		renewIfNeededAndGetToken();
		LOGGER.info("remove current channel by using channelid: [{}]", channelId);
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.REMOVE)
				.queryParam("channelId", channelId)
				.queryParam("token", token.getTokenId());
		client.target(uriBuilder).request().delete();
		LOGGER.info("Deleting the following channels: [{}]", channelId);
	}

	public List<MessageChannel> getChannelParticipants(String channelId)
			throws JsonParseException, JsonMappingException, IOException, WebApplicationException {
		renewIfNeededAndGetToken();
		LOGGER.info("Retrieving participants of channel: [{}] with token: [{}]", channelId, token);
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.MESSAGE_CHANNEL_RESOURCE)
				.path(ServerConstants.MESSAGE_CHANNEL_PARTICIPANTS).queryParam("channelId", channelId)
				.queryParam("token", token.getTokenId());
		Response response = client.target(uriBuilder).request().get();
		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		String data = response.readEntity(String.class);
		List<MessageChannel> channels = MAPPER.readValue(data,
				MAPPER.getTypeFactory().constructCollectionType(List.class, MessageChannel.class));
		LOGGER.info("Received the following channel participants: [{}]", channels);
		return channels;
	}

	public RealTimeSessionConfiguration requestGameSession(SessionType sessionType) throws WebApplicationException {
		renewIfNeededAndGetToken();
		LOGGER.info("Attempting to request a game session of type: [{}] with token: [{}]", sessionType, token);
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.REAL_TIME_RESOURCE).path(ServerConstants.NEW)
				.queryParam("token", token.getTokenId()).queryParam("session", sessionType);
		Response response = client.target(uriBuilder).request().post(Entity.json(""));
		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		RealTimeSessionConfiguration configuration = response.readEntity(RealTimeSessionConfiguration.class);
		LOGGER.info("Creation successful. Received configuration: [{}]", configuration);
        client.target(uriBuilder).request().delete();
		return configuration;
	}

	public List<RealTimeSessionConfiguration> getActiveGames()
			throws JsonParseException, JsonMappingException, IOException, WebApplicationException {
		return getActiveGames(null);
	}

	/**
	 * Gets the multiplayer session for the given the session ID
	 *
	 * @param sessionID the session ID to get
	 */
	public RealTimeSessionConfiguration getMultiplayerSession(String sessionID) {
		doMSCCacheFunctions();
		return sessionIDMap.getOrDefault(sessionID, null);
	}


	/**
	 * Gets the session ID for an MSC RTSC
	 *
	 * @param configuration the config to get the session ID for
	 */
	public String getSessionID(RealTimeSessionConfiguration configuration) {
		doMSCCacheFunctions();
		LOGGER.debug("Attempting to get session ID for RTSC [{}]", configuration.toString());

		// get an existing quick ID
		if (sessionIDMap.containsValue(configuration)) {
			// not MSC
			return null;
		}

		for (String sessionID : sessionIDMap.keySet()) {
			if (sessionIDMap.get(sessionID).equals(configuration)) {
				return sessionID;
			}
		}

		return null;
	}

	/**
	 * Searches for multiplayer sessions like the given search query.
	 *
	 * @param sessionType
	 * @param search
	 * @return
	 */
	public ArrayList<RealTimeSessionConfiguration> searchSessionID(SessionType sessionType, String search) {
		doMSCCacheFunctions();
		ArrayList<RealTimeSessionConfiguration> mscList = new ArrayList<>();

		if (search == null || search.isEmpty()) {
			return mscList;
		}

		for (RealTimeSessionConfiguration rtsc : sessionIDMap.values()) {
			if (rtsc.getSession() != sessionType) {
				continue;
			}

			if (rtsc.getSessionID().contains(search) || search.contains(rtsc.getSessionID())) {
				mscList.add(rtsc);
			}
		}

		return mscList;
	}

	/**
	 * Gets a multiplayer game session with associated ID and quick connect ID
	 *
	 * @param sessionType the session type of the game
	 * @param sessionID   the session ID for the game
	 */
	public RealTimeSessionConfiguration requestMultiplayerGameSession(SessionType sessionType, String sessionID) {
		doMSCCacheFunctions();

		Random random = new Random();
		String UUID = sessionID;

		try {
			quickConnectSemaphore.acquire();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		int retries = 10000;
		for (int i = 0; i < retries && getMultiplayerSession(UUID) != null; ++i) {
			UUID = sessionID + String.valueOf(random.nextInt(9999 + i));
			if (i + 1 == retries) {
				quickConnectSemaphore.release();
				throw new RuntimeException("No unique sessions can be created with that name!");
			}
		}

		renewIfNeededAndGetToken();
		LOGGER.info("Attempting to request a named game session of type: [{}] with token: [{}]", sessionType, token);
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.REAL_TIME_RESOURCE).path(ServerConstants.NEW)
				.queryParam("token", token.getTokenId()).queryParam("session", sessionType).queryParam("sessionID", sessionID);
		Response response = client.target(uriBuilder).request().post(Entity.json(""));
		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		RealTimeSessionConfiguration configuration = response.readEntity(RealTimeSessionConfiguration.class);
		LOGGER.info("Creation successful. Received MSC configuration: [{}]", configuration);

		quickConnectSemaphore.release();

		return configuration;
	}

	public List<RealTimeSessionConfiguration> getActiveGames(SessionType sessionType)
			throws JsonParseException, JsonMappingException, IOException, WebApplicationException {
		String session = sessionType == null ? "" : sessionType.toString();
		renewIfNeededAndGetToken();
		LOGGER.info("Attempting to retrieve all active game session of type: [{}] with token: [{}]", sessionType,
				token);
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.REAL_TIME_RESOURCE)
				.queryParam("token", token.getTokenId()).queryParam("session", session);
		Response response = client.target(uriBuilder).request().get();
		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		String data = response.readEntity(String.class);
		List<RealTimeSessionConfiguration> configurations = MAPPER.readValue(data,
				MAPPER.getTypeFactory().constructCollectionType(List.class, RealTimeSessionConfiguration.class));
		LOGGER.info("Retrieved active sessions filtered  by [{}]: {}", session, configurations);
		return configurations;
	}

	public String getUsername() {
		return username;
	}

	/**
	 * Runs {@link #isServerAlive(int)} with {@link ServerConstants#REST_ADMIN_PORT} as its parameter}
	 *
	 * @return true if the server is alive and healthy, else false. Note that the server may be running if this returns
	 * false, but will not be healthy.
	 */
	public boolean isServerAlive() {
		return isServerAlive(ServerConstants.REST_ADMIN_PORT);
	}

	/**
	 * Returns true if the given session is named
	 */
	public boolean isSessionNamed(RealTimeSessionConfiguration rtsc) {
		return rtsc.getSessionID() != null;
	}

	/**
	 * Checks whether a given dropwizard instance is alive. Must be given the admin port of the dropwizard instance.
	 *
	 * @param port The admin port of the dropwizard instance running on the host defined in this client.
	 * @return true if the server is alive and healthy, else false. Note that the server may be running if this returns
	 * false, but will not be healthy.
	 */
	public boolean isServerAlive(int port) {
		UriBuilder uriBuilder = rootUriBuilder(port).path("healthcheck");
		try {
			Response response = client.target(uriBuilder).request().get();
			if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
				return false;
			}
		} catch (ProcessingException exception) {
			return false;
		}
		return true;
	}
}
