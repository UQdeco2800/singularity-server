package uq.deco2800.singularity.server.dangernoodle;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.common.representations.dangernoodle.DisconnectedPlayer;
import uq.deco2800.singularity.common.representations.dangernoodle.GameState;
import uq.deco2800.singularity.common.representations.dangernoodle.PlayersInCurrentLobby;
import uq.deco2800.singularity.common.representations.dangernoodle.SimpleMessage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by khoi_truong on 2016/10/18.
 * <p>
 * This class is used to handle messages that were sent to the server side on
 * the server itself. Each instance of this class is used to handle a
 * separate lobby, so that no illegal communication will be done between two
 * clients in two separate lobbies.
 */
public class DangernoodleRealTimeServerListener extends Listener {
    private final static String CLASS = DangernoodleRealTimeServerListener.class.getName();
    private final static Logger LOGGER = LoggerFactory.getLogger(CLASS);
    // Constant used to check if this lobby is full or not.
    private final static int PLAYERS_PER_LOBBY = 2;
    // Keep track of who's connecting to the server at the moment.
    private HashMap<Connection, String> players = new HashMap<>();
    private ArrayList<String> playerIDs = new ArrayList<>();
    // Game state
    private GameState gameState = GameState.START_LOBBY;
    // Counter to keep track of the number of players who have done reading
    // the instructions. Upon finishing, the client will send a simple
    // message to the server telling that it has done doing so.
    private int instructionReleased = 0;
    private boolean playingGame = false;

    @Override
    public void connected(Connection connection) {
        if (!players.containsKey(connection)) {
            // Repel joiners who attempt to join the lobby by tell them that the
            // lobby is full.
            if (gameState == GameState.START_GAME || gameState == GameState.PLAY_GAME) {
                // Disconnect the newly connected player after telling it that it
                // has joined a full lobby.
                connection.sendTCP(GameState.LOBBY_FULL);
                disconnected(connection);
            }
        } else {
            disconnected(connection);
        }
    }

    @Override
    public void disconnected(Connection connection) {
        LOGGER.info("[{}] disconnected.", players.get(connection));
        connection.close();
        if (players.containsKey(connection)) {
            // Tell the others that a player has quit the game.
            if (gameState == GameState.PLAY_GAME) {
                announceDisconnectedPlayer(players.get(connection));
            } else {
                String disconnectedPlayer = players.get(connection);
                playerIDs.remove(disconnectedPlayer);
                players.remove(connection);
                LOGGER.debug("Removed {} from lobby", disconnectedPlayer);
                LOGGER.info("Current size is now {}", playerIDs);
                // Send back number of players currently in the game
                PlayersInCurrentLobby message = new PlayersInCurrentLobby();
                message.setPlayers(playerIDs);
                // Tell all the players currently in the game that a new
                // player has just joined the lobby.
                for (Connection player : players.keySet()) {
                    player.sendTCP(message);
                }
            }
        }
    }

    @Override
    public void received(Connection client, Object object) {
        // Check for number of players in the lobby. If the lobby is full,
        // proceed to play.
        if (players.keySet().size() <= PLAYERS_PER_LOBBY) {
            // Lobby is currently vacant, so more players can still join.
            if (gameState == GameState.START_LOBBY) {
                // If the to-be-connected client send client ID, accept it.
                if (object instanceof String) {
                    String playerID = (String) object;
                    players.put(client, playerID);
                    playerIDs.add(playerID);
                    LOGGER.info("Client " + playerID + " has connected to the lobby.");
                    LOGGER.info("Current size is now {}", players.keySet().size());
                    // Send back number of players currently in the game
                    PlayersInCurrentLobby message = new PlayersInCurrentLobby();
                    message.setPlayers(playerIDs);
                    // Tell all the players currently in the game that a new
                    // player has just joined the lobby.
                    for (Connection player : players.keySet()) {
                        player.sendTCP(message);
                    }
                    // Once the lobby is full, start the game.
                    if (players.keySet().size() == PLAYERS_PER_LOBBY) {
                        // Tell all the players that the game has started.
                        gameState = GameState.START_GAME;
                        announceStartGame();
                        LOGGER.info("Game is now started.");
                        // Tell all the players that they are now playing the game.
                        gameState = GameState.PLAY_GAME;
                        announcePlayGame();
                        LOGGER.info("Players can now play the game.");
                    }
                } else if (object instanceof GameState) {
                    GameState clientRequest = (GameState) object;
                    // Disconnect the player if leave lobby is requested.
                    if (clientRequest == GameState.LEAVE_LOBBY) {
                        disconnected(client);
                    }
                }
            } else if (gameState == GameState.PLAY_GAME) {
                // Until all players have done reading, do not start the game.
                // Clients will not start the game (they are still being
                // paused) until all clients are done.
                if (!playingGame) {
                    if (instructionReleased < PLAYERS_PER_LOBBY) {
                        if (object instanceof SimpleMessage) {
                            if (object == SimpleMessage.INSTRUCTION_RELEASED) {
                                instructionReleased++;
                            }
                        }
                    } else {
                        playingGame = true;
                        // Once all players have done reading instructions, tell
                        // every one to start the game and from now on, any
                        // message will be broadcast to other players.
                        for (Connection connection : players.keySet()) {
                            connection.sendTCP(SimpleMessage.START_PLAYING);
                        }
                    }
                } else {
                    // Now anything that is sent to this server must be broadcast to
                    // other players. Simply go through all connections and send the
                    // object.
                    for (Connection connection : players.keySet()) {
                        if (!connection.equals(client)) {
                            connection.sendTCP(object);
                        }
                    }
                }
            }
        }
    }

    /**
     * This function is used to tell all players within this lobby that the
     * game has now started.
     */
    private void announceStartGame() {
        for (Connection connection : players.keySet()) {
            connection.sendTCP(GameState.START_GAME);
        }
    }

    /**
     * This function is used to tell all players within this lobby that they
     * are now able to play the game.
     */
    private void announcePlayGame() {
        for (Connection connection : players.keySet()) {
            connection.sendTCP(GameState.PLAY_GAME);
        }
    }

    /**
     * This function is used to announce to all other players that a player
     * has left the lobby.
     *
     * @param disConnectedPlayer
     *         the player who left the lobby
     *
     * @throws NullPointerException
     *         if disConnectedPlayer is null
     * @require disConnectedPlayer != null
     * @ensure message is sent to all other players
     */
    private void announceDisconnectedPlayer(String disConnectedPlayer) {
        if (disConnectedPlayer == null) {
            throw new NullPointerException("Disconnected player ID cannot be null.");
        }
        for (Connection player : players.keySet()) {
            player.sendTCP(SimpleMessage.PLAYER_LEFT);
        }
    }
}
