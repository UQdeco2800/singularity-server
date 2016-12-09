package uq.deco2800.singularity.common.representations.coaster;

/**
 * Created by khoi_truong on 2016/10/18.
 * Updated for coaster by RyanCarrier on 2016/10/19
 * <p>
 * This enumeration is used to handle the switching between different game
 * state on this server.
 */
public enum GameState {
	EMPTY_LOBBY,
	START_LOBBY,
	JOINED_LOBBY,
	LOBBY_FULL,
	START_GAME,
	END_GAME,
}
