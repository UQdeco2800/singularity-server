package uq.deco2800.singularity.common.representations.duxcom.gamestate;

/**
 * The game state message type that is being sent.
 *
 * Created by liamdm on 9/10/2016.
 */
public enum GameStateMessageType {
    /**
     * Query data regarding the game
     */
    GAME_METADATA_QUERY,
    /**
     * Used by an admin to control the game state
     */
    CONTROL_MESSAGE,
    /**
     * Register for a game session
     */
    GAME_REGISTRATION_MESSAGE,
    /**
     * Register for a state change
     */
    STATE_CHANGE,
    /**
     * Message used to control gameplay
     */
    GAME_UPDATE,
    /**
     * Player actions to the server
     */
    PLAYER_ACTION,
    /**
     * A squad state message
     */
    SQUAD_STATE
}
