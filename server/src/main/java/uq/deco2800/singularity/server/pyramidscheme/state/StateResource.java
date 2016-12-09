package uq.deco2800.singularity.server.pyramidscheme.state;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import javax.activity.InvalidActivityException;
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
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.pyramidscheme.GameState;
import uq.deco2800.singularity.server.authentication.TokenDao;
import uq.deco2800.singularity.server.user.UserDao;
import uq.deco2800.singularity.server.user.UserResource;

/**
 * Control the state of the game and how it is loaded and unloaded.
 * 
 * @author tris10au
 * 
 */
@Path(ServerConstants.PYRAMID_STATE_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class StateResource {
	private static final String CLASS = UserResource.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	private final StateDao stateDao;
	private final TokenDao tokenDao;
	
	public StateResource(StateDao stateDao, TokenDao tokenDao) {
		if(stateDao == null) {
			throw new NullPointerException("Parameter (stateDao) must not be null");
		}
		if(tokenDao == null) {
			throw new NullPointerException("Parameter (tokenDao) must not be null");
		}
		
		this.stateDao = stateDao;
		this.tokenDao = tokenDao;
	}
	
	/**
	 * Gets the latest game state for the logged in user.
	 * @param tokenId
	 * @return the current GameState
	 */
	@GET
	public GameState getLatestState(
	         @QueryParam("token") Optional<String> tokenId) {
		
		if(!tokenId.isPresent() || tokenId.get().isEmpty()) {
			throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
		}
		
		Token token = tokenDao.findByTokenId(tokenId.get());
		if(token == null || token.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
		}
		
		List<GameState> gameStates = stateDao.findCurrentGameState(token.getUserId());
		
		if (gameStates == null || gameStates.size() == 0) {
			throw new WebApplicationException("No game stae could be found", Status.NOT_FOUND);
		}
		
		return gameStates.get(0);
	}
	
	@POST
	@Path(ServerConstants.NEW)
	public Response saveState(String payload, @QueryParam("token") Optional<String> tokenID) 
				throws IOException, NoSuchAlgorithmException, InvalidActivityException {
		
		if(!tokenID.isPresent() || tokenID.get().isEmpty()) {
			throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
		}
		
		Token token = tokenDao.findByTokenId(tokenID.get());
		
		if(token == null || token.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
		}
		
		GameState gameState = new GameState();
		// Validate the payload is valid
		// @TODO Add game validation and correctness check
		
		String stateID = UUID.randomUUID().toString();
		while(stateDao.findByStateID(stateID) != null) {
			stateID = UUID.randomUUID().toString();
		}
		
		gameState.setStateID(stateID);
		gameState.setUserID(token.getUserId());
		gameState.setSaveTime();
		gameState.setData(payload);
		
		stateDao.insert(gameState);
		
		return Response.status(Status.CREATED).entity(stateID).build();
	}

}
