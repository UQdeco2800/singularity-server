package uq.deco2800.singularity.server.duxcom.multiplayer;

/**
 * The game was in an invalid state when a command was called.
 *
 * Created by liamdm on 11/10/2016.
 */
public class InvalidGamestateException extends RuntimeException{
    public InvalidGamestateException(String message) {
        super(message);
    }
}
