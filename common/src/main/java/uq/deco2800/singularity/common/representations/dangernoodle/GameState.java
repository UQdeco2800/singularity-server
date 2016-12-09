package uq.deco2800.singularity.common.representations.dangernoodle;

/**
 * Created by khoi_truong on 2016/10/18.
 * <p>
 * This enumeration is used to handle the switching between different game
 * state on this server.
 */
public enum GameState {
    START_LOBBY,
    JOINED_LOBBY,
    REQUEST_CLIENT_ID,
    LEAVE_LOBBY,
    REQUEST_CLIENTS_IN_LOBBY,
    LOBBY_FULL,
    START_GAME,
    PLAY_GAME,
    END_GAME,
    TURN_CHANGED,
    NOODLE_MOVED,
    NOODLE_FIRED
}
