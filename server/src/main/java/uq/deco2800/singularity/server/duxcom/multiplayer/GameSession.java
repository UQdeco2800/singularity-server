package uq.deco2800.singularity.server.duxcom.multiplayer;

import com.esotericsoftware.kryonet.Connection;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.AbstractGameStateMessage;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.GameState;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Stores an instance of a game session.
 *
 * Created by liamdm on 11/10/2016.
 */
public class GameSession {
    /**
     * The player who's turn it is
     */
    private String currentPlayerTurn;

    /**
     * The state of the game
     */
    private GameState gameState;

    /**
     * If the current game is ditched
     */
    private boolean ditched = false;

    /**
     * Returns true if the admin has left.
     */
    private boolean adminLeft = false;

    /**
     * The name of the creator
     */
    private String creatorName;

    /**
     * The admins connection
     */
    private Connection creatorConnection;

    /**
     * The in-game joined people
     */
    private HashMap<String, Connection> joined = new HashMap<>();

    /**
     * The order of players
     */
    private LinkedList<String> playerOrder = new LinkedList<>();

    /**
     * The quick name map, not maintained
     */
    private HashMap<Connection, String> nameMap = new HashMap<>();

    /**
     * The name of the current map
     */
    private String mapName;

    public GameSession() {
        gameState = GameState.WAIT;
    }

    /**
     * Gets the current game state
     * @return the current game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Returns true if the server is in a joinable state
     * @return iff the server is in a joinable state
     */
    public boolean canJoin(){
        return this.gameState == GameState.LOBBY;
    }

    /**
     * Returns true if the server is in an unestablished
     * state and can be created by an admin.
     * @return iff the session can be created
     */
    public boolean canCreate(){
        return this.gameState == GameState.WAIT;
    }

    /**
     * Signal this session to be ditched.
     */
    public void ditch(){
        ditched = true;
    }

    /**
     * Mark the session as created
     * @param creator the creator of the session
     * @param creatorConnection the creators connection
     */
    public void create(String creator, Connection creatorConnection){
        this.currentPlayerTurn = creator;
        this.creatorName = creator;
        playerOrder.add(creator);
        this.creatorConnection = creatorConnection;
        this.gameState = GameState.UNINITIALISED;
    }

    /**
     * Returns a list of the users joined in the game session
     * @return the users joined.
     */
    public List<String> getJoinedUsers(){
        return playerOrder;
    }

    /**
     * Returns true if the user is in the game session
     * @param username the username to check
     * @return if the username is in game session
     */
    public boolean isInGameSession(String username){
        return joined.containsKey(username) || creatorName.equals(username);
    }

    /**
     * Returns true if the given user is an admin
     * @param username the username to check
     * @return if the user is in the game session
     */
    public boolean isAdmin(String username){
        return creatorName.equals(username);
    }

    /**
     * Request to join a session
     * @param requesterUsername the username of the joiner
     * @param connection the connection of the joiner
     */
    public void join(String requesterUsername, Connection connection) {
        checkAdmin();
        joined.put(requesterUsername, connection);
        playerOrder.add(requesterUsername);

        this.currentPlayerTurn = playerOrder.get(0);

        nameMap.put(connection, requesterUsername);
    }

    /**
     * Checks if the admin exists and if not throw the admin loss exception.
     */
    private void checkAdmin() {
        if(adminLeft) {
            throw new AdminLossException();
        }
    }

    /**
     * Register a user leaving the game.
     * @param username the username that left
     * @throws AdminLossException thrown if it is an admin that is leaving
     */
    public void leave(String username) throws AdminLossException {
        if(isAdmin(username)){
            throw new AdminLossException();
        }
        joined.remove(username);
    }

    /**
     * Register a user leaving the game.
     * @throws AdminLossException thrown if it is an admin that is leaving
     */
    public void leave(Connection connection) throws AdminLossException {
        // check admin
        if(connection.equals(creatorConnection)){
            leave(creatorName);
        }

        // check name map
        if(nameMap.containsKey(connection)){
            leave(nameMap.get(connection));
            return;
        }

        // slow check
        for(String name : joined.keySet()){
            if(joined.get(name).equals(connection)){
                leave(name);
                return;
            }
        }
    }

    /**
     * Returns true if the current game is ditched
     * @return if the game is ditched
     */
    public boolean isDitched() {
        return gameState == GameState.FINISHED
                || gameState == GameState.DITCHED
                || gameState == GameState.CLOSED;
    }

    /**
     * Returns true if the given connection is for an admin
     * @param connection the connection of the admin
     * @return if the connection is of an admin
     */
    public boolean isAdmin(Connection connection) {
        return connection.equals(creatorConnection);
    }

    /**
     * Gets the current players turn
     */
    public String getCurrentPlayerTurn(){
        return currentPlayerTurn;
    }

    /**
     * Moves to the next player
     */
    public String nextPlayer(){
        int currentIndex = playerOrder.indexOf(getCurrentPlayerTurn());
        ++currentIndex;
        if(currentIndex == playerOrder.size()){
            currentIndex = 0;
        }

        currentPlayerTurn = playerOrder.get(currentIndex);
        return currentPlayerTurn;
    }

    /**
     * Opens the game lobby
     */
    public void openLobby() {
        if(gameState !=  GameState.UNINITIALISED){
            throw new InvalidGamestateException(String.format("Game must be UNINITIALISED when moving to lobby, actual state: %s", String.valueOf(gameState)));
        }

        this.gameState = GameState.LOBBY;
    }
    
    /**
     * Gets a set of connections in this game session
     * @return the connections in this game session
     */
    public List<Connection> getConnections(){
        List<Connection> values = new LinkedList<Connection>(){{
            addAll(joined.values());
            add(creatorConnection);
        }};
        return values;
    }

    /**
     * Starts a game and returns the game initialisation vector
     */
    public String startGame(String mapName) {
        this.gameState = GameState.IN_GAME;
        this.mapName = mapName;
        return String.valueOf(UUID.randomUUID());
    }

    /**
     * Gets the name of the current map
     */
    public String getMapName() {
        return mapName;
    }
}
