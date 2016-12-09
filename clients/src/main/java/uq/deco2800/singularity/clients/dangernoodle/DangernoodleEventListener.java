package uq.deco2800.singularity.clients.dangernoodle;

/**
 * Created by khoi_truong on 2016/10/21.
 * <p>
 * This class is used by the client to broadcast any received message from
 * the server to all listeners.
 */
public abstract class DangernoodleEventListener {
    /**
     * Main method that is overridden in the implementation class which will
     * be used to tell the class that the server just sent back something.
     */
    public void notifyListener() {
    }

    /**
     * Method that is overriden in the implementation class which will be
     * used to tell a class that the server has send given obj back.
     *
     * @param obj
     *         the object that the server has sent back to the client
     */
    public void notifyListener(Object obj) {
    }
}
