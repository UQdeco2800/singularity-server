package uq.deco2800.singularity.server.messaging;

import com.codahale.metrics.health.HealthCheck;

public class MessagingServiceHealthCheck extends HealthCheck {
	
	private MessagingService messagingService;
	
	public MessagingServiceHealthCheck(MessagingService service) {
		super();
		messagingService = service;
	}
	
	@Override
	protected Result check() throws Exception {
		if (messagingService.isStarted()) {
			return Result.healthy();
		}
		return Result.unhealthy("Waiting on messaging service to start");
	}
}
