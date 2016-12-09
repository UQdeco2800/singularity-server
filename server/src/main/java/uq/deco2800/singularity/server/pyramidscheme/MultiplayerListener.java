package uq.deco2800.singularity.server.pyramidscheme;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.common.representations.pyramidscheme.multiplayer.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles connections between the server and the player and broadcasts the appropriate responses to each player
 */
public class MultiplayerListener extends Listener {
    private final static String CLASS = MultiplayerListener.class.getName();
    private final static Logger LOGGER = LoggerFactory.getLogger(CLASS);
    Timer timer = new Timer();
    Integer uid = 1;
    String currentUserTurn = "";
    String username1 = "";
    String username2 = "";
    boolean taggedPlayer1 = false;
    boolean taggedPlayer2 = false;
    private HashMap<Connection, String> players = new HashMap<>();
    private MultiplayerState gameState = MultiplayerState.LOBBY;
    private String pingUsernameResponse = "";

    @Override
    public void connected(Connection connection) {
        super.connected(connection);
    }

    /**
     * Broadcasts to players, informing them that a player forfeited. The
     * listener shall subsequently close _both_ players connections,
     * and reset itself into the LOBBY state, so that two new players can join
     * and initiate new matches.
     */
    private void playerForfeited() {
        LOGGER.debug("Sending forfeit and closing clients");
        for (Connection player : players.keySet()) {
            PlayerForfeited forfeited = new PlayerForfeited();
            player.sendTCP(forfeited);
            player.close();
        }
        // Allow new connections
        gameState = MultiplayerState.LOBBY;
        // Reset timer
        timer = new Timer();
        taggedPlayer1 = false;
        taggedPlayer2 = false;
    }

    private void playGame() throws InterruptedException {
        LOGGER.info("LOADING game");
        gameState = MultiplayerState.GAME_INIT;
        for (Connection connection : players.keySet()) {
            connection.sendTCP(MultiplayerState.PLAYING);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pingListeners();
            }
        }, 0, 1000);
    }

    private void pingListeners() {
        if (gameState == MultiplayerState.PLAYING) {
            return;
        }
        LOGGER.info("pinging players");
        for (Connection connection : players.keySet()) {
            PlayerToken token = new PlayerToken();
            connection.sendTCP(token);
        }
    }

    /**
     * Given a deck that has been tagged with UIDs following a tagDeck call,
     * broadcasts this _new_ deck to players
     *
     * @param deck A deck thas been appropriately tagged with UIDs
     */
    private void sendDeck(PlayerDeck deck) {
        LOGGER.info("sending deck");
        for (Connection connection : players.keySet()) {
            connection.sendTCP(deck);
        }
    }

    /**
     * Broadcasts to players, informing them that it is time to send their
     * decks to Singularity for UID tagging.
     */
    private void requestDecks() {
        LOGGER.info("requesting deck");
        gameState = MultiplayerState.SEND_DECKS;
        for (Connection connection : players.keySet()) {
            connection.sendTCP(MultiplayerState.SEND_DECKS);
        }
    }

    /**
     * Broadcasts to players informing them that they should transition
     * from the multiplayer lobby to the game screen.
     *
     * Randomly chooses a player to go first, allowing currentTurn calls to
     * succeed and not send the "" string.
     */
    private void startGame() {
        LOGGER.info("starting game");
        for (Connection connection : players.keySet()) {
            connection.sendTCP(MultiplayerState.PLAYING);
        }
        currentUserTurn = ThreadLocalRandom.current().nextInt(0, 1) == 0 ? username1 : username2;
    }

    /**
     * Broadcasts to clients informing them of the username of the player
     * whose turn it currently is.
     */
    private void currentTurn() {
        LOGGER.debug("sending current turn");
        for (Connection connection : players.keySet()) {
            Turn turn = new Turn();
            turn.username = currentUserTurn;
            connection.sendTCP(turn);
        }
    }

    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);
        if (players.containsKey(connection)) {
            players.remove(connection);
            if (gameState == MultiplayerState.PLAYING) {
                playerForfeited();
            }
        }
    }

    /**
     * The main method shim. This method represents the bulk of the servers
     * business logic, and makes to broadcast methods to respond to clients.
     *
     * It will:
     *
     * - Terminate the connections of players who have forfeited
     * - Open a lobby that:
     *     + Waits for at least two players to join
     *     + Notifies both players when a match is ready
     * - Ask players for their decks to be tagged with UIDs
     * - Tag their decks and exchange them
     * - Forwards on player actions to their opponents
     *
     * @param client The client that sent the object
     * @param object The object recieved from the client
     */
    @Override
    public void received(Connection client, Object object) {
        super.received(client, object);
        // Player will send their username
        if (object instanceof PlayerForfeited) {
            client.close();
            return;
        }
        if (gameState == MultiplayerState.LOBBY) {
            // Check if username
            if (object instanceof String) {
                LOGGER.info("username received " + object);
                String username = (String) object;
                players.put(client, username);
                if (players.keySet().size() == 2) {
                    username2 = username;
                    LOGGER.info("Lobby now full, awaiting status request to start");
                    gameState = MultiplayerState.OTHER_PLAYER_CONNECTING;
                    for (Map.Entry<Connection, String> keys : players.entrySet()) {
                        if (keys.getValue().equals(username)) {
                            keys.getKey().sendTCP(username2);
                        } else {
                            keys.getKey().sendTCP(username);
                        }
                    }
                } else {
                    username1 = username;
                    LOGGER.info("player joined, waiting for another");
                }
            } else {
                LOGGER.error("Received some anonymous object from client.");
            }
        } else if (object instanceof PlayerToken && gameState.equals(MultiplayerState.GAME_INIT)) {
            LOGGER.debug("Comparing " + ((PlayerToken) object).username + " to " + pingUsernameResponse);
            if (!((PlayerToken) object).username.equals(pingUsernameResponse) && !((PlayerToken) object).username.equals("")) {
                timer.cancel();
                requestDecks();
            }
        } else if (object instanceof PlayerDeck && gameState == MultiplayerState.SEND_DECKS) {
            if (((PlayerDeck) object).username.equals(username1) && !taggedPlayer1) {
                sendDeck(tagDeck((PlayerDeck) object));
                taggedPlayer1 = true;
            } else if (((PlayerDeck) object).username.equals(username2) && !taggedPlayer2) {
                sendDeck(tagDeck((PlayerDeck) object));
                taggedPlayer2 = true;
            }
            if (taggedPlayer1 && taggedPlayer2) {
                if (gameState != MultiplayerState.PLAYING) {
                    gameState = MultiplayerState.PLAYING;
                    startGame();
                }
            }
        } else if (gameState.equals(MultiplayerState.OTHER_PLAYER_CONNECTING)) {
            // Start the game
            if (object instanceof MultiplayerState) {
                if (object.equals(MultiplayerState.STATUS_REQUEST)) {
                    LOGGER.info("Received status request, starting game");
                    try {
                        playGame();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (gameState == MultiplayerState.PLAYING) {
            LOGGER.debug("sending data to other player");
            if (object instanceof ChampionAbility || object instanceof GrindCard
                    || object instanceof PlayCard || object instanceof PlayerForfeited
                    || object instanceof PyramidRefill || object instanceof GameOver) {
                // Send all received objects to the other player
                players.keySet().stream().filter(connection -> !connection.equals(client)).forEach(connection ->
                        connection.sendTCP(object));
            } else if (object instanceof Turn) {
                currentTurn();
            } else if (object instanceof PassTurn) {
                Attack attack = new Attack();
                attack.username = currentUserTurn;
                players.keySet().stream().filter(connection -> !connection.equals(client)).forEach(connection ->
                        connection.sendTCP(attack));
                currentUserTurn = ((PassTurn) object).username.equals(username2) ? username1 : username2;
                currentTurn();
            }
        } else if (object instanceof MultiplayerState) {
            // tell others to gtfo
            client.sendTCP(MultiplayerState.LOBBY_FULL);
            client.close();
        }
    }

    /**
     * Given a PlayerDeck, replaces its UIDs with increasing successive positive
     * integers.
     *
     * Multiple calls to this method will not cause the method to distribute
     * already assigned UIDs.
     *
     * @param playerDeck The PlayerDeck to be tagged
     * @return The tagged PlayerDeck
     */
    private PlayerDeck tagDeck(PlayerDeck playerDeck) {
        for (Map<String, Integer> serverCard : playerDeck.deck) {
            Map.Entry<String, Integer> card = serverCard.entrySet().iterator().next();
            card.setValue(uid);
            uid++;
        }
        return playerDeck;
    }
}
