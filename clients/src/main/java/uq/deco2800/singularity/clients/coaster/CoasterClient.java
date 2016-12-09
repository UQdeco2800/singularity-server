package uq.deco2800.singularity.clients.coaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.coaster.Score;
import uq.deco2800.singularity.common.representations.coaster.ScoreType;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;
import java.util.List;


public class CoasterClient extends SingularityRestClient {

	private static final String CLASS = CoasterClient.class.getName();

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	private Token token;

	private static final String TOKENPARAM = "token";

	public CoasterClient() {
		super();
	}

	public CoasterClient(String host, int port) {
		super(host, port);
	}

	/**
	 * Get a list of all time high scores regardless of player. Limited to top 10
	 *
	 * @param type Score type to filter by.
	 * @return List of Scores top scores given of type.
	 */
	public List<Score> getHighestScores(ScoreType type) {
		LOGGER.info("Attempting to retrieve list of Coaster high scores by: [{}]", type.name());

		// Update token if needed. 
		this.token = renewIfNeededAndGetToken();

		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.SCORE_RESOURCE)
				.queryParam(TOKENPARAM, token.getTokenId())
				.queryParam("type", type.name());

		Response response = client.target(uriBuilder).request().get();

		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}

		List<Score> scores = response.readEntity(new GenericType<List<Score>>() {

		});

		LOGGER.info("Successfully retrieved information: [{}]", scores);
		return scores;
	}


	/**
	 * Get a list of high scores ordered by type for given user. Limited to 10
	 *
	 * @param userId UserId to filter by.
	 * @param type   Score type to filter by.
	 * @return List of 10 scores ranked by scoretype for given userId
	 */
	public List<Score> getHighestScores(String userId, ScoreType type) throws WebApplicationException {
		LOGGER.info("Attempting to retrieve list of Coaster high scores for user: [{}], by type: [{}]", userId, type.toString());

		// Update token if needed. 
		this.token = renewIfNeededAndGetToken();

		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.SCORE_RESOURCE)
				.queryParam(TOKENPARAM, token.getTokenId())
				.queryParam("type", type.name())
				.queryParam("userId", userId);
		Response response = client.target(uriBuilder).request().get();

		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}

		List<Score> scores = response.readEntity(new GenericType<List<Score>>() {

		});

		LOGGER.info("Successfully retrieved information: [{}]", scores);

		return scores;
	}

	/**
	 * Return the top of all time highscore regardless of player for given type.
	 *
	 * @param type Score type to filter by.
	 * @return All time highest game score for given type.
	 */
	public Score getHighestScore(ScoreType type) {
		LOGGER.info("Attempting to retrieve all time of Coaster high scores by type: [{}]", type.toString());
		return getHighestScores(type).get(0);
	}

	/**
	 * Get Single Highest score for user by Given Type.
	 *
	 * @param userId User Id to filter by.
	 * @param type   Type of score to filter by.
	 * @return Score
	 * @throws JsonProcessingException
	 * @throws WebApplicationException
	 */
	public Score getHighestScore(String userId, ScoreType type) throws WebApplicationException {
		return getHighestScores(userId, type).get(0);
	}

	/**
	 * Create high scores to server to be added to database, validation is handled server side.
	 * Expect server response with UUID for score.
	 *
	 * @param score
	 * @throws JsonProcessingException
	 */
	public Score createHighScore(Score score) throws JsonProcessingException {

		LOGGER.info("Attempting to submit Coaster high score: [{}]", score);

		// Update token if needed. 
		this.token = renewIfNeededAndGetToken();

		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.SCORE_RESOURCE).path(ServerConstants.NEW).queryParam(TOKENPARAM, token.getTokenId());
		String data = MAPPER.writeValueAsString(score);

		Response response = client.target(uriBuilder).request().post(Entity.json(data));

		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}

		String scoreId = response.readEntity(String.class);
		score.setScoreId(scoreId);
		LOGGER.info("Successfully created score: [{}]", score);

		return score;
	}


}


