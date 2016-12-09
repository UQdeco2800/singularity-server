package uq.deco2800.singularity.server.realtime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;
import uq.deco2800.singularity.common.util.ServerUtils;
import uq.deco2800.singularity.server.authentication.TokenDao;

@Path(ServerConstants.REAL_TIME_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class RealTimeResource {

	private TokenDao tokenDao;
	private Map<SessionType, List<RealTimeService>> activeSessions;

	private static final String CLASS = RealTimeService.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);
	private static final int MAX_BIND_TRIES = 39;

	public RealTimeResource(TokenDao tokenDao) {
		this.tokenDao = tokenDao;
		activeSessions = new HashMap<SessionType, List<RealTimeService>>();
		for (SessionType sessionType : SessionType.values()) {
			activeSessions.put(sessionType, new LinkedList<RealTimeService>());
		}
	}

	@GET
	public Response getGames(@QueryParam("token") Optional<String> tokenId,
			@QueryParam("session") Optional<String> session) {
		if (!tokenId.isPresent() || tokenId.get().isEmpty()) {
			throw new WebApplicationException("A token is required as a query parameter", Status.FORBIDDEN);
		}
		Token token = tokenDao.findByTokenId(tokenId.get());
		if (token == null || token.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("Token provided is invalid or has expired", Status.FORBIDDEN);
		}
		if (session.isPresent() && !session.get().isEmpty()) {
			SessionType sessionType = null;
			try {
				sessionType = SessionType.valueOf(session.get());
			} catch (IllegalArgumentException exception) {
				throw new WebApplicationException("Session type given is invalid", Status.BAD_REQUEST);
			}
			List<RealTimeSessionConfiguration> configurations = new LinkedList<>();
			for (RealTimeService activeSession : activeSessions.get(sessionType)) {
				configurations.add(activeSession.getConfiguration());
			}
			return Response.ok(configurations).build();
		}
		List<RealTimeSessionConfiguration> configurations = new LinkedList<>();
		for (SessionType sessionType : SessionType.values()) {
			for (RealTimeService activeSession : activeSessions.get(sessionType)) {
				configurations.add(activeSession.getConfiguration());
			}
		}
		return Response.ok(configurations).build();

	}

	@POST
	@Path(ServerConstants.NEW)
	public Response createGame(String payload, @QueryParam("token") Optional<String> tokenId,
			@QueryParam("session") Optional<String> session,
							   @QueryParam("sessionID") Optional<String> sessionID) {
		if (!tokenId.isPresent() || tokenId.get().isEmpty() || !session.isPresent() || session.get().isEmpty()) {
			throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
		}
		SessionType sessionType = null;
		try {
			sessionType = SessionType.valueOf(session.get());
		} catch (IllegalArgumentException exception) {
			throw new WebApplicationException("Session is an invalid value: " + session, Status.BAD_REQUEST);
		}
		Token token = tokenDao.findByTokenId(tokenId.get());
		if (token == null || token.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
		}
		RealTimeSessionConfiguration configuration = new RealTimeSessionConfiguration();
		configuration.setSession(sessionType);
		if(sessionID.isPresent()) {
			configuration.setSessionID(sessionID.get());
		}
		int attempts = 0;
		while (true) {
			if (attempts++ > MAX_BIND_TRIES) {
				LOGGER.warn("Could not bind after {} number of tries for configuration [{}]", MAX_BIND_TRIES,
						configuration);
				throw new WebApplicationException("Could not create game session", Status.SERVICE_UNAVAILABLE);
			}
			int port = ServerUtils.getAvailablePort();
			if (port < 0) {
				LOGGER.warn("Could not get next available port");
				throw new WebApplicationException("Could not create game session", Status.SERVICE_UNAVAILABLE);
			}
			configuration.setPort(port);
			RealTimeService service = getRealTimeServiceFromSessionType(sessionType, configuration);
			if (service.start()) {
				activeSessions.get(sessionType).add(service);
				break;
			}
		}

		return Response.ok(configuration).build();
	}

	private RealTimeService getRealTimeServiceFromSessionType(SessionType sessionType,
			RealTimeSessionConfiguration configuration) {
		RealTimeService service;
		switch (sessionType) {
		case DANGER_NOODLES:
			service = new DangernoodlesSession(configuration);
			break;
		case COASTER:
			service = new CoasterSession(configuration);
			break;
		case DUXCOM:
			service = new DuxcomSession(configuration);
			break;
		case PYRAMID_SCHEME:
			service = new PyramidschemeSession(configuration);
			break;
		case DUCKTALES:
			service = new DucktalesSession(configuration);
			break;
		default:
			throw new WebApplicationException("Cannot create game session requested as it is not a valid option",
					Status.BAD_REQUEST);
		}
		return service;
	}

	public void shutdownLiveServices() {
		for (List<RealTimeService> sessionLists : activeSessions.values()) {
			for (RealTimeService session : sessionLists) {
				session.stop();
			}
		}
	}
}
