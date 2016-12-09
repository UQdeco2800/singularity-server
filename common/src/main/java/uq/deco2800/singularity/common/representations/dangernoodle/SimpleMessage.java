package uq.deco2800.singularity.common.representations.dangernoodle;

/**
 * Created by khoi_truong on 2016/10/23.
 * <p>
 * This enum is used to signal between the server and the client with simple
 * message that is followed by a concrete set of action. It's used to tell
 * the server and client about something without the need to include the detail
 * itself.
 */
public enum SimpleMessage {
    INSTRUCTION_RELEASED,
    START_PLAYING,
    PLAYER_LEFT,
    PAUSED
}
