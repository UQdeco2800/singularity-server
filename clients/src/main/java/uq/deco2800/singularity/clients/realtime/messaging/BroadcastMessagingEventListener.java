package uq.deco2800.singularity.clients.realtime.messaging;

import uq.deco2800.singularity.common.representations.realtime.BroadcastMessage;

/**
 * Event for the receipt of message events
 *
 * Created by liamdm on 21/09/2016.
 */
public abstract class BroadcastMessagingEventListener {

    public abstract void recievedBroadcastMessage(BroadcastMessage message);

    public abstract void disconnected();
}
