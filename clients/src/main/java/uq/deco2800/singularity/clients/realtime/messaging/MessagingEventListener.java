package uq.deco2800.singularity.clients.realtime.messaging;

import java.util.EventListener;

import uq.deco2800.singularity.common.representations.realtime.IncomingMessage;

public abstract class MessagingEventListener implements EventListener {
	
	public abstract void didReceiveMessage(IncomingMessage message);
	
}
