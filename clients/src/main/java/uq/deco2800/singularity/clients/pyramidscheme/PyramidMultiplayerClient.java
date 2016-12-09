package uq.deco2800.singularity.clients.pyramidscheme;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.clients.realtime.RealTimeClient;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.pyramidscheme.multiplayer.*;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Pyramid
 */
public class PyramidMultiplayerClient extends RealTimeClient {

    private static final String CLASS = PyramidMultiplayerClient.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

    private MultiplayerListener multiplayerListener = new MultiplayerListener();

    private String username;


    /**
     * @param configuration
     * @param client
     * @throws IOException
     */
    public PyramidMultiplayerClient(RealTimeSessionConfiguration configuration, SingularityRestClient client) throws IOException {
        super(configuration, client);
        realTimeClient.addListener(this.multiplayerListener);
        this.username = restClient.getUsername();
        realTimeClient.sendTCP(username);
    }

    public void sendPlayerTurn(int cardUID, int x, int y) {
        PlayCard playCard = new PlayCard();
        playCard.username = username;
        playCard.cardUID = cardUID;
        playCard.x = x;
        playCard.y = y;
        LOGGER.info("Sending turn");
        realTimeClient.sendTCP(playCard);
    }

    public void sendGrindCard(int cardUID) {
        GrindCard grindCard = new GrindCard();
        grindCard.username = username;
        grindCard.cardUID = cardUID;
        realTimeClient.sendTCP(grindCard);
    }

    public void sendPyramidRefill(int size) {
        PyramidRefill pyramidRefill = new PyramidRefill();
        pyramidRefill.username = username;
        pyramidRefill.size = size;
        realTimeClient.sendTCP(pyramidRefill);
    }

    public void passTurn() {
        PassTurn passTurn = new PassTurn();
        passTurn.username = username;
        realTimeClient.sendTCP(passTurn);
    }

    public void sendChampionAbility(String championName, Integer abilityID, int targetCardUID) {
        ChampionAbility championAbility = new ChampionAbility();
        championAbility.username = username;
        championAbility.championName = championName;
        championAbility.abilityID = abilityID;
        championAbility.targetCardUID = targetCardUID;
        realTimeClient.sendTCP(championAbility);
    }

    public void sendAttack() {
        Attack attack = new Attack();
        attack.username = username;
        realTimeClient.sendTCP(attack);
    }

    public void sendPlayerDeck(ArrayList<Map<String, Integer>> deck) {
        PlayerDeck playerDeck = new PlayerDeck();
        playerDeck.username = username;
        playerDeck.deck = deck;
        realTimeClient.sendTCP(playerDeck);
    }

    public void sendForfeit() {
        PlayerForfeited playerForfeited = new PlayerForfeited();
        realTimeClient.sendTCP(playerForfeited);
    }

    public void sendGameOver(boolean userWon) {
        GameOver gameOver = new GameOver();
        gameOver.username = username;
        gameOver.userWon = userWon;
    }

    public void getStatus() {
        LOGGER.debug("requesting status of lobby");
        realTimeClient.sendTCP(MultiplayerState.STATUS_REQUEST);
    }

    public void sendToken(String username) {
        LOGGER.debug("Sending username");
        PlayerToken token = new PlayerToken();
        token.username = username;
        realTimeClient.sendTCP(token);
    }

    public void getTurn() {
        LOGGER.debug("Getting turn");
        Turn turn = new Turn();
        turn.username = username;
        realTimeClient.sendTCP(turn);
    }

    /**
     * @return
     */
    @Override
    public SessionType getSessionType() {
        return SessionType.PYRAMID_SCHEME;
    }

    /**
     * @param listener
     */
    public void addListener(PyramidMultiplayerListener listener) {
        multiplayerListener.addListener(listener);
    }

    /**
     * @param listener
     */
    public void removeListener(PyramidMultiplayerListener listener) {
        multiplayerListener.removeListener(listener);
    }

    private class MultiplayerListener extends Listener {

        private List<PyramidMultiplayerListener> listeners = new LinkedList<>();

        public MultiplayerListener() {

        }
        @Override
        public void received(Connection connection, Object object) {
            if (object instanceof MultiplayerState) {
                MultiplayerState state = (MultiplayerState) object;
                switch (state) {
                    case LOBBY_FULL:
                        LOGGER.info("Lobby full");
                        for (PyramidMultiplayerListener listener : listeners) {
                            listener.lobbyFull();
                        }
                        break;
                    case SEND_DECKS:
                        for (PyramidMultiplayerListener listener : listeners) {
                            listener.sendDeck();
                        }
                        break;
                    case PLAYING:
                        LOGGER.info("Starting a multiplayer game");
                        for (PyramidMultiplayerListener listener : listeners) {
                            listener.startGame();
                        }
                        LOGGER.info("Game started");
                        break;
                    case STATUS_REQUEST:
                        break;
                    case LOBBY:
                        realTimeClient.sendTCP(username);
                        for (PyramidMultiplayerListener listener : listeners) {
                            listener.lobbyFound();
                        }
                        LOGGER.info("joined lobby");
                        break;
                }
            } else if (object instanceof KeepAlive) {
                LOGGER.trace("Received keep alive: [{}]", object);
            } else if (object instanceof PlayerDeck) {
                for (PyramidMultiplayerListener listener : listeners) {
                    listener.deckReceived((PlayerDeck) object);
                }
            } else if (false) {
                LOGGER.info("Received unexpected object: [{}]", object);
            } else if (object instanceof ChampionAbility) {
                for (PyramidMultiplayerListener listener : listeners) {
                    listener.championAbility((ChampionAbility) object);
                }
            } else if (object instanceof GrindCard) {
                for (PyramidMultiplayerListener listener : listeners) {
                    listener.grindCard((GrindCard) object);
                }
            } else if (object instanceof PassTurn) {
                for (PyramidMultiplayerListener listener : listeners) {
                    listener.passTurn((PassTurn) object);
                }
            } else if (object instanceof PlayCard) {
                for (PyramidMultiplayerListener listener : listeners) {
                    listener.playCard((PlayCard) object);
                }
            } else if (object instanceof PlayerForfeited) {
                for (PyramidMultiplayerListener listener : listeners) {
                    listener.playerForfeited((PlayerForfeited) object);
                }
            } else if (object instanceof PyramidRefill) {
                for (PyramidMultiplayerListener listener : listeners) {
                    listener.pyramidRefill((PyramidRefill) object);
                }
            } else if (object instanceof PlayerToken) {
                for (PyramidMultiplayerListener listener : listeners) {
                    listener.sendPlayerToken();
                }
            } else if (object instanceof Turn) {
                for (PyramidMultiplayerListener listener : listeners) {
                    listener.getTurn((Turn) object);
                }
            } else if (object instanceof Attack) {
                for (PyramidMultiplayerListener listener : listeners) {
                    listener.attack((Attack) object);
                }
            } else if (object instanceof GameOver) {
                for (PyramidMultiplayerListener listener : listeners) {
                    listener.nowGameOver((GameOver) object);
                }
            }
        }

        /**
         * @param listener
         */
        public void addListener(PyramidMultiplayerListener listener) {
            listeners.add(listener);
        }

        /**
         * @param listener
         */
        public void removeListener(PyramidMultiplayerListener listener) {
            listeners.remove(listener);
        }
    }

}
