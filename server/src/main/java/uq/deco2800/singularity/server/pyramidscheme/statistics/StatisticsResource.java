package uq.deco2800.singularity.server.pyramidscheme.statistics;

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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import io.dropwizard.jackson.Jackson;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.pyramidscheme.UserStatistics;
import uq.deco2800.singularity.server.authentication.TokenDao;
import uq.deco2800.singularity.server.user.UserDao;
import uq.deco2800.singularity.server.user.UserResource;

@Path(ServerConstants.STATISTICS_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsResource {
	private static final String CLASS = UserResource.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	// The Data Access object to interface with the User database.
	private final StatisticsDao statDao;
	private final UserDao userDao;
	private final TokenDao tokenDao;
	
	public StatisticsResource(StatisticsDao statDao, UserDao userDao, TokenDao tokenDao) {
		if(statDao == null) {
			throw new NullPointerException("Parameter (statDao) must not be null");
		}
		if(userDao == null) {
			throw new NullPointerException("Parameter (userDao) must not be null");
		}
		if(tokenDao == null) {
			throw new NullPointerException("Parameter (tokenDao) must not be null");
		}
		
		this.statDao = statDao;
		this.userDao = userDao;
		this.tokenDao = tokenDao;
	}
	
	/**
	 * Gets the User statistics for a given userID
	 * @param userID
	 * @param tokenId
	 * @return UserStatistics
	 */
	@GET
	public List<UserStatistics> getUserCurrentStatistics(
			 @QueryParam("userId") Optional<String> userID,
	         @QueryParam("token") Optional<String> tokenId) {
		
		if(!tokenId.isPresent() || tokenId.get().isEmpty()) {
			throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
		}
		
		Token token = tokenDao.findByTokenId(tokenId.get());
		if(token == null || token.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
		}
		
		List<UserStatistics> userStats = null;
		
		if(userID.isPresent()) {
			userStats = this.statDao.findCurrentStatistics(userID.get());
		}
		
		if(userStats != null) {
			return userStats;
		} else {
			throw new WebApplicationException("User statistics could not" +
					"be found for userID:" + userID, Status.NOT_FOUND);
		}
	}
	
	@POST
	@Path(ServerConstants.NEW)
	public Response createNewUserStats(String payload, @QueryParam("token") Optional<String> tokenID) 
				throws IOException, NoSuchAlgorithmException, InvalidActivityException {
		
		if(!tokenID.isPresent() || tokenID.get().isEmpty()) {
			throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
		}
		
		Token token = tokenDao.findByTokenId(tokenID.get());
		
		if(token == null || token.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
		}
		
		UserStatistics newUserStats = null;
		
		try {
			newUserStats = MAPPER.readValue(payload, UserStatistics.class);
		} catch (JsonParseException | JsonMappingException e) {
			throw new WebApplicationException("Malformed request", Status.BAD_REQUEST);
		}
		
		// Check the user exists
		try {
			userDao.findById(newUserStats.getUserID());
		} catch (Exception e) {
			throw new WebApplicationException("User doesn't exist", Status.NOT_FOUND);
		}
		
		String statID = UUID.randomUUID().toString();
		while(statDao.findByStatID(statID) != null) {
			statID = UUID.randomUUID().toString();
		}
		
		newUserStats.setStatID(statID);
		
		statDao.insert(newUserStats);
		
		return Response.status(Status.CREATED).entity(statID).build();
	}

}
