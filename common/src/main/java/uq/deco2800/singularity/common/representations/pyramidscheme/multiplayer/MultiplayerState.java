package uq.deco2800.singularity.common.representations.pyramidscheme.multiplayer;

/**
 * Created by nick on 22/10/16.
 */
public enum MultiplayerState {
    LOBBY_FULL, // Tell other players to go away
    SEND_DECKS, // Initiate swapping of decks
    PLAYING, // Changes screen from lobby/ start refill of pyramid
    STATUS_REQUEST, // Sent on connect to trigger screen change
    GAME_INIT, // Initial state after the game screen load has begun
    OTHER_PLAYER_CONNECTING, // Waiting for other player to trigger a status_request
    LOBBY
}
