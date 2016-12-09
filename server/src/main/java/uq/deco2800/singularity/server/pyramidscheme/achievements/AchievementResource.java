package uq.deco2800.singularity.server.pyramidscheme.achievements;

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
import uq.deco2800.singularity.common.representations.pyramidscheme.Achievements;
import uq.deco2800.singularity.server.authentication.TokenDao;
import uq.deco2800.singularity.server.user.UserDao;
import uq.deco2800.singularity.server.user.UserResource;

@Path(ServerConstants.ACHIEVEMENT_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class AchievementResource {
	private static final String CLASS = UserResource.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	// The Data Access object to interface with the User database.
	private final AchievementDao achieveDao;
	private final UserDao userDao;
	private final TokenDao tokenDao;
	
	public AchievementResource(AchievementDao achieveDao, UserDao userDao, TokenDao tokenDao) {
		if(achieveDao == null) {
			throw new NullPointerException("Parameter (achieveDao) must not be null");
		}
		if(userDao == null) {
			throw new NullPointerException("Parameter (userDao) must not be null");
		}
		if(tokenDao == null) {
			throw new NullPointerException("Parameter (tokenDao) must not be null");
		}
		
		this.achieveDao = achieveDao;
		this.userDao = userDao;
		this.tokenDao = tokenDao;
	}
	
	/**
	 * Gets the achievements for a given user id
	 * @param userID
	 * @param tokenId
	 * @return Achievements
	 */
	@GET
	public List<Achievements> getAchievements(
			 @QueryParam("userId") Optional<String> userID,
	         @QueryParam("token") Optional<String> tokenId) {
		
		if(!tokenId.isPresent() || tokenId.get().isEmpty()) {
			throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
		}
		
		Token token = tokenDao.findByTokenId(tokenId.get());
		if(token == null || token.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
		}
		
		List<Achievements> achieves  = null;
		
		if(userID.isPresent()) {
			achieves = this.achieveDao.findAllStatistics(userID.get());
		}
		
		if(achieves != null) {
			return achieves;
		} else {
			throw new WebApplicationException("Achievements could not be found for userID:" + userID, Status.NOT_FOUND);
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
		
		Achievements newAchieve = null;
		
		try {
			newAchieve = MAPPER.readValue(payload, Achievements.class);
		} catch (JsonParseException | JsonMappingException e) {
			throw new WebApplicationException("Malformed request", Status.BAD_REQUEST);
		}
		
		// Check the user exists
		try {
			userDao.findById(newAchieve.getUserID());
		} catch (Exception e) {
			throw new WebApplicationException("User doesn't exist", Status.NOT_FOUND);
		}
		
		String statID = UUID.randomUUID().toString();
		while(achieveDao.findByStatID(statID) != null) {
			statID = UUID.randomUUID().toString();
		}
		
		newAchieve.setStatID(statID);
		newAchieve.setTimestamp();
		
		achieveDao.insert(newAchieve);
		
		return Response.status(Status.CREATED).entity(statID).build();
	}

}
