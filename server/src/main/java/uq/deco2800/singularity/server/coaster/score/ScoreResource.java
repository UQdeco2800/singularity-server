package uq.deco2800.singularity.server.coaster.score;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.UUID;

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
import uq.deco2800.singularity.common.representations.coaster.Score;
import uq.deco2800.singularity.common.representations.coaster.ScoreType;
import uq.deco2800.singularity.server.authentication.TokenDao;
import uq.deco2800.singularity.server.user.UserDao;
import uq.deco2800.singularity.server.user.UserResource;

@Path(ServerConstants.SCORE_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class ScoreResource {
	private static final String CLASS = UserResource.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	// The Data Access object to interface with the User database.
	private final ScoreDao scoreDao;
	private final UserDao userDao;
	private final TokenDao tokenDao;

	public ScoreResource(ScoreDao scoreDao, UserDao userDao, TokenDao tokenDao) {

		if (scoreDao == null) {
			throw new NullPointerException("Parameter (scoreDao) must not be null");
		}
		if (userDao == null) {
			throw new NullPointerException("Parameter (userDao) must not be null");
		}
		if (tokenDao == null) {
			throw new NullPointerException("Parameter (tokenDao) must not be null");
		}

		this.userDao = userDao;
		this.scoreDao = scoreDao;
		this.tokenDao = tokenDao;
	}

	@GET
	public List<Score> getScores(@QueryParam("type") Optional<ScoreType> type,
			@QueryParam("userId") Optional<String> userId, @QueryParam("token") Optional<String> tokenId) {

		LOGGER.debug("Getting scores by type.");

		if (!tokenId.isPresent() || tokenId.get().isEmpty()) {
			throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
		}

		Token token = tokenDao.findByTokenId(tokenId.get());
		if (token == null || token.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
		}

		List<Score> scores = null;
		if (type.isPresent()) {
			if (userId.isPresent()) {
				switch (type.get()) {
				case EXPERIENCE:
					scores = scoreDao.findHighestScoresByExperienceAndId(userId.get());
					break;
				case TIME:
					scores = scoreDao.findHighestScoresByPlayTimeAndId(userId.get());
					break;
				case KILLS:
					scores = scoreDao.findHighestScoresByKillsAndId(userId.get());
					break;
				case BOSSES:
					scores = scoreDao.findHighestScoresByBossKillsAndId(userId.get());
					break;
				case WORTH:
					scores = scoreDao.findHighestScoresByNetWorthAndId(userId.get());
					break;
				default:
					break;
				}

				if (scores != null) {
					return scores;
				} else {
					throw new WebApplicationException("Scores not found for type " + type + " and userId " + userId,
							Status.NOT_FOUND);
				}
			} else {
				switch (type.get()) {
				case EXPERIENCE:
					scores = scoreDao.findHighestScoresByTypeExperience();
					break; 
				case TIME:
					scores = scoreDao.findHighestScoresByTypePlayTime();
					break;
				case KILLS:
					scores = scoreDao.findHighestScoresByTypeKills();
					break;
				case BOSSES:
					scores = scoreDao.findHighestScoresByTypeBossKills();
					break;
				case WORTH:
					scores = scoreDao.findHighestScoresByTypeNetWorth();
					break;
				default:
					break;
				}
				if (scores != null) {
					return scores;
				} else {
					throw new WebApplicationException("Scores not found for type " + type, Status.NOT_FOUND);
				}
			}
		} else {
			throw new WebApplicationException("Type or Type and userId required as query parameter",
					Status.BAD_REQUEST);
		}
	}

	@POST
	@Path(ServerConstants.NEW)
	public Response createNewScore(String payload, @QueryParam("token") Optional<String> tokenId)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		LOGGER.debug("Creating score in database.");

		if (!tokenId.isPresent() || tokenId.get().isEmpty()) {
			throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
		}

		Token token = tokenDao.findByTokenId(tokenId.get());
		if (token == null || token.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
		}

		Score newScore = null;
		try {
			newScore = MAPPER.readValue(payload, Score.class);
		} catch (JsonParseException | JsonMappingException e) {
			throw new WebApplicationException("Malformed request", Status.BAD_REQUEST);
		}

		// Check user exists.
		try {
			userDao.findById(newScore.getUserId());
		} catch (Exception e) {
			throw new WebApplicationException("User doesn't exist", Status.NOT_FOUND);
		}
		String scoreId = UUID.randomUUID().toString();
		while (scoreDao.findById(scoreId) != null) {
			scoreId = UUID.randomUUID().toString();
		}
		newScore.setScoreId(scoreId);

		if (Integer.parseInt(newScore.getExperience()) <= 0) {
			throw new WebApplicationException("Experience cannot be negative or zero.", Status.BAD_REQUEST);
		}

		scoreDao.insert(newScore);
		return Response.status(Status.CREATED).entity(scoreId).build();
	}

}
