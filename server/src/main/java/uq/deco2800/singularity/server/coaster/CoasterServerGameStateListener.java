package uq.deco2800.singularity.server.coaster;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.common.representations.coaster.GameState;
import uq.deco2800.singularity.common.representations.coaster.state.DisconnectedPlayer;
import uq.deco2800.singularity.common.representations.coaster.state.EndGame;
import uq.deco2800.singularity.common.representations.coaster.state.NewPlayer;
import uq.deco2800.singularity.server.realtime.CoasterSession;

import java.util.HashMap;

/**
 * Created by khoi_truong on 2016/10/18.
 * Updated for coaster by RyanCarrier on 2016/10/19
 * <p>
 * This class is used to handle messages that were sent to the server side on
 * the server itself.
 */
//This will currentl ybe used as the game itself also until we implement multiple games
public class CoasterServerGameStateListener extends Listener {
	private final static String CLASS = CoasterServerGameStateListener.class.getName();
	private final static Logger LOGGER = LoggerFactory.getLogger(CLASS);
	// Constant used to check if this lobby is full or not.
	private final static int PLAYERS_PER_LOBBY = 2;
	// Keep track of who's connecting to the server at the moment.
	private HashMap<Connection, NewPlayer> players = new HashMap<>();

	private Connection host = null;
	// Game state
	private GameState gameState = GameState.EMPTY_LOBBY;

	private CoasterSession session;
	//private NewPlayer host;

	private int tickrate;

	public CoasterServerGameStateListener(CoasterSession session) {
		this.session = session;
	}

	@Override
	public void connected(Connection connection) {
		super.connected(connection);
		LOGGER.info("Connected" + connection.toString());
		// Check for number of players in the lobby. If the lobby is full,
		// proceed to play.
		if (players.keySet().size() <= PLAYERS_PER_LOBBY) {
			// Do not add the new connection here. Only add it once it has
			// confirmed the connection by sending back its name.
			connection.sendTCP(GameState.JOINED_LOBBY);
		} else {
			// Disconnect the newly connected player after telling it that it
			// has joined a full lobby.
			connection.sendTCP(GameState.LOBBY_FULL);
			disconnected(connection);
		}
	}

	@Override
	public void disconnected(Connection connection) {
		super.disconnected(connection);
		LOGGER.info("Player disconnect");
		// Remove the disconnected client from the list of clients and tell
		// the others that one client has disconnected, this only happens
		// inside the session that the client is currently connecting to.
		if (players.containsKey(connection)) {
			NewPlayer disconnectedPlayer = players.remove(connection);
			// If the game is still playing and someone disconnect, tell
			// others about the disconnected player.
			if (gameState == GameState.START_GAME) {
				announceDisconnectedPlayer(disconnectedPlayer);
			}
		}
		if (players.keySet().size() == 0) {
			gameState = GameState.EMPTY_LOBBY;
			//LOGGER.info("Shutting down session.");
			//this.session.stop();
		}
	}

	@Override
	public void received(Connection client, Object object) {
		super.received(client, object);
		LOGGER.info("Coaster received State:" + gameState + " Object:" + object.getClass().toGenericString());
		// Only allow receiving stuffs once the game has started.
		switch (gameState) {
			//empty lobby or game over
			//need to check to ensure disconnected after game
			case END_GAME:
			case EMPTY_LOBBY:
				if (object instanceof NewPlayer) {
					host = client;
					//tickrate = ((NewPlayer) object).getTick();
					players.put(client, ((NewPlayer) object));
					setState(GameState.START_LOBBY);
				} else {
					unknownObjectRecieved(object);
				}
				break;
			case START_LOBBY:
				if (object instanceof NewPlayer) {
					players.put(client, ((NewPlayer) object));
					// Once the lobby is full, start the game.
					if (players.keySet().size() == PLAYERS_PER_LOBBY) {
						setState(GameState.START_GAME);
					}
					host.sendTCP(object);
					client.sendTCP(players.get(host));
				} else {
					unknownObjectRecieved(object);
				}
				break;
			case START_GAME:
				//game is running update users
				announce(object);
				if (object instanceof EndGame) {
					setState(GameState.END_GAME);
				}
				break;
			default:
				//other??
				break;

		}

	}

	private void unknownObjectRecieved(Object o) {
		LOGGER.error("Received some anonymous object from client. class:" + o.getClass().toGenericString());
	}

	private void announceExcept(Object o, Connection c) {
		for (Connection connection : players.keySet()) {
			if (!connection.equals(c)) {
				connection.sendTCP(o);
			}
		}
	}

	private void announce(Object o) {
		//players.forEach(c -> c.sendTCP(o));
		for (Connection connection : players.keySet()) {
			connection.sendTCP(o);
		}
	}

	private void announceState() {
		announce(gameState);
	}


	private void setState(GameState gs) {
		LOGGER.info("Coaster state changed to " + gs.toString());
		gameState = gs;
		announceState();
	}

	/**
	 * This function is used to announceState to all other players that a player
	 * has left the lobby.
	 *
	 * @param d the player who left the lobby
	 * @throws NullPointerException if disConnectedPlayer is null
	 * @require disConnectedPlayer != null
	 * @ensure message is sent to all other players
	 */
	private void announceDisconnectedPlayer(NewPlayer d) {
		for (Connection player : players.keySet()) {
			player.sendTCP(new DisconnectedPlayer(d));
		}
	}
}
