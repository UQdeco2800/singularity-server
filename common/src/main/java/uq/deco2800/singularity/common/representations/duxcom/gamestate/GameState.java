package uq.deco2800.singularity.common.representations.duxcom.gamestate;

/**
 * The state the game is in.
 *
 * Created by liamdm on 9/10/2016.
 */
public enum GameState {
    /**
     * The game is not established yet
     */
    WAIT,
    /**
     * The game session has been joined by an admin but is not open to lobby
     */
    UNINITIALISED,
    /**
     * The game session is in a lobby and waiting players, can join in this state
     */
    LOBBY,
    /**
     * The game is in play
     */
    IN_GAME,
    /**
     * The game is finished but the session is remaining open.
     */
    FINISHED,
    /**
     * The admin left the game and everyone connected should disconnect
     */
    DITCHED,
    /**
     * The game is finished and the session is closed.
     */
    CLOSED
}
