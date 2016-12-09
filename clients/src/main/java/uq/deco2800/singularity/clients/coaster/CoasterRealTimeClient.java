package uq.deco2800.singularity.clients.coaster;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.clients.realtime.RealTimeClient;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.coaster.GameState;
import uq.deco2800.singularity.common.representations.coaster.state.GameStateUpdate;
import uq.deco2800.singularity.common.representations.coaster.state.NilUpdate;
import uq.deco2800.singularity.common.representations.coaster.state.Update;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;
import uq.deco2800.singularity.common.util.KryoUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by khoi_truong on 2016/10/16.
 * <p>
 * Updated for coaster by RyanCarrier on 2016/10/19
 * <p>
 * This class is used by the client to talk to the server. More specifically,
 * it's called inside the game's ClientManager to retrieve an instance of
 * this class. With it the client can send updates to the server.
 */
public class CoasterRealTimeClient extends RealTimeClient {
	// Setting up loggers to be used throughout the file.
	private static final String CLASS = CoasterRealTimeClient.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	private GameState currentState;
	// ID of this particular client
	private String clientID;

	private RealTimeSessionConfiguration config;

	private CoasterClient restClient;

	private LinkedList<Update> updates = new LinkedList<>();


	/*


		HOST 	movementUpdate>
				fireUpdate>
				bullet shot>
				damage taken>
				<fireUpdate
				<movementUpdate
							SERVER
		CLIENT  <movementUpdate
				<fireUpdate
				<damage taken
				movementUpdate>
				fireUpdate>

	 */

	/**
	 * Default constructor for the class.
	 *
	 * @param config     real time configuration of the lobby which the client wants to
	 *                   talk to
	 * @param restClient restful client which is used in the parent class
	 * @throws NullPointerException if config, restClient is null
	 * @throws IOException          if some I/O operation has failed
	 * @require config != null && restClient != null && sesstionType != null
	 * @ensure new instance of this class
	 */
	public CoasterRealTimeClient(RealTimeSessionConfiguration config,
	                             CoasterClient restClient)
			throws IOException {
		// Relay back the arguments to the parent class
		this(config, restClient, ServerConstants.PRODUCTION_SERVER);
	}

	public CoasterRealTimeClient(RealTimeSessionConfiguration config,
	                             CoasterClient restClient, String host)
			throws IOException {
		// Relay back the arguments to the parent class
		super(config, restClient, host);
		if (config == null || restClient == null) {
			throw new NullPointerException("Real time configuration and " +
					"restful client must not be null.");
		}
		this.config = config;
		this.restClient = restClient;
		realTimeClient.addListener(new RealTimeListener());
		clientID = restClient.renewIfNeededAndGetToken().getUserId();
		Kryo kryo = realTimeClient.getKryo();
		KryoUtils.registerCommonClasses(kryo);

	}

	public void init(RealTimeSessionConfiguration configuration, SingularityRestClient restClient, String server) throws IOException {
		LOGGER.info("Starting real time client with configuration: [{}]", configuration);
		super.restClient = restClient;
		realTimeClient = new Client();
		realTimeClient.start();
		KryoUtils.registerCommonClasses(realTimeClient.getKryo());

		realTimeClient.connect(8000, server, configuration.getPort());
		//Log.set(Log.LEVEL_TRACE);
		LOGGER.info("Initiated");
	}


	public CoasterRealTimeClient(CoasterClient restClient)
			throws IOException {
		this(DefaultConfig(), restClient);
	}

	public static RealTimeSessionConfiguration DefaultConfig() {
		RealTimeSessionConfiguration configuration = new RealTimeSessionConfiguration();
		configuration.setPort(ServerConstants.MESSAGING_PORT);
		configuration.setSession(SessionType.COASTER);
		return configuration;
	}

	public void sendUpdate(Update u) {
		//LOGGER.info("trying to send update of type", u.getClass().toGenericString());
		realTimeClient.sendTCP(u);
	}


	private void checkErrors(String playerId) {
		if (currentState != GameState.START_GAME) {
			illegalUpdate();
			return;
		}
		if (playerId == null) {
			throw new NullPointerException("Player Id cannot be null.");
		}
	}

	private void illegalUpdate() {
		LOGGER.info("Cannot update in current state; " + currentState);
	}

	public Update tickRecieve() {
		try {
			return updates.pop();
		} catch (NoSuchElementException e) {
			return new NilUpdate();
		}
	}


	/**
	 * Return the game type which this client is playing.
	 *
	 * @return the sessionType which this client is playing
	 */
	@Override
	public SessionType getSessionType() {
		return this.config.getSession();
	}


	/**
	 * Private internal class which is used to handle communication between
	 * this client and the server.
	 */
	private class RealTimeListener extends Listener {
		@Override
		public void received(Connection connection, Object object) {
			LOGGER.info("recieved object");
			super.received(connection, object);
			// If the message from the server is a game state message,
			// indicating which state this client and the game is in
			// currently, then update the global game state.
			if (object instanceof Update) {
				LOGGER.info("Update recieved " + object.getClass().toGenericString());
				updates.add((Update) object);

			} else if (object instanceof GameState) {
				currentState = (GameState) object;
				updates.add(new GameStateUpdate(currentState));
				LOGGER.info("GameState update; " + currentState);
			} else if (object instanceof FrameworkMessage.KeepAlive) {
				LOGGER.info("recieved keep alive");
			} else {
				LOGGER.error("Uknown object recieved" + object.getClass().toGenericString());
			}

		}
	}
}