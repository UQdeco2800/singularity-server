package uq.deco2800.singularity.clients.pyramidscheme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.pyramidscheme.UserStatistics;
import uq.deco2800.singularity.common.representations.pyramidscheme.Achievements;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PyramidSchemeClient extends SingularityRestClient {

	private static final String CLASS = PyramidSchemeClient.class.getName();
	
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	private Token token;
	
	public PyramidSchemeClient() {
		super();
	}
	
	public PyramidSchemeClient(String host, int port) {
		super(host, port);
	}
	
	/**
	 * Gets the current User statistics for a given userID
	 * @param userID
	 * 			User we want the statistics for
	 * @return the User's current statistics (UserStatistics)
	 */
	public UserStatistics getCurrentStatistics(String userID) throws WebApplicationException{
		UserStatistics finalstats;
		LOGGER.info("Attempting to retrieve current statistics of userId: [{}]", userID);
		
		// Update token if required
		this.token = renewIfNeededAndGetToken();
		
		UriBuilder uribuilder = rootUriBuilder().path(ServerConstants.STATISTICS_RESOURCE)
				.queryParam("token", token.getTokenId())
				.queryParam("userId", userID);
		
		Response response = client.target(uribuilder).request().get();
		
		if(response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		
		List<UserStatistics> userStats = (List<UserStatistics>) response.readEntity(new GenericType<List<UserStatistics>>(){});
		
		if(userStats.isEmpty()) {
			finalstats = null;
		} else {
			finalstats = userStats.get(0);
			LOGGER.info("Successfully retrieved user statistics");
		}
		
		return finalstats;
	}
	
	/**
	 * Gets the achievements for a given userID
	 * @param userID
	 * 			User we want the achieves for
	 * @return the User's achieves (Achievements)
	 */
	public List<Achievements> getAchievements(String userID) throws WebApplicationException{
		LOGGER.info("Attempting to retrieve achievements of userId: [{}]", userID);
		
		// Update token if required
		this.token = renewIfNeededAndGetToken();
		
		UriBuilder uribuilder = rootUriBuilder().path(ServerConstants.ACHIEVEMENT_RESOURCE)
				.queryParam("token", token.getTokenId())
				.queryParam("userId", userID);
		
		Response response = client.target(uribuilder).request().get();
		
		if(response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		
		List<Achievements> achieves = (List<Achievements>) response.readEntity(new GenericType<List<Achievements>>(){});
		
		LOGGER.info("Successfully retrieved achievements");
		
		return achieves;
	}
	
	/**
	 * Gets the current User statistics for a given userID
	 * @param userID
	 * 			User we want the statistics for
	 * @param champName
	 * 			The champion statistics we want
	 * @return the User's current statistics (ChampionStatistics)
	 */
	public ChampionStatistics getCurrentChampStatistics(String userID, String champName) throws WebApplicationException{
		ChampionStatistics finalstats;
		LOGGER.info("Attempting to retrieve current statistics of [{}] with userId: [{}]", champName, userID);
		
		// Update token if required
		this.token = renewIfNeededAndGetToken();
		
		UriBuilder uribuilder = rootUriBuilder().path(ServerConstants.CHAMP_STATISTICS_RESOURCE)
				.queryParam("token", token.getTokenId())
				.queryParam("userId", userID)
				.queryParam("name", champName);
		
		Response response = client.target(uribuilder).request().get();
		
		if(response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		
		List<ChampionStatistics> champStats = (List<ChampionStatistics>) response.readEntity(
				new GenericType<List<ChampionStatistics>>(){});
		
		if(champStats.isEmpty()) {
			finalstats = null;
		} else {
			finalstats = champStats.get(0);
			LOGGER.info("Successfully retrieved user statistics");
		}
		
		return finalstats;
	}
	
	/**
	 * Creates the user statistics and adds it to the server
	 * @param userStats
	 * 			userStatistics to be added to the server
	 * @return the current userStatistics
	 * @throws WebApplicationException
	 * @throws JsonProcessingException
	 */
	public UserStatistics createUserStats(UserStatistics userStats) throws WebApplicationException, JsonProcessingException  {
		
		LOGGER.info("Attempting to submit User stats: [{}]", userStats);
		
		//Update token if required
		this.token = renewIfNeededAndGetToken();
		
		UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.STATISTICS_RESOURCE).path(ServerConstants.NEW)
				.queryParam("token", token.getTokenId());
		
		String data = MAPPER.writeValueAsString(userStats);
		
		Response response = client.target(uriBuilder).request().post(Entity.json(data));
		
		if(response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			throw new WebApplicationException(response);
		}
		
		String statsID = response.readEntity(String.class);
		userStats.setStatID(statsID);
		LOGGER.info("Successfully created User statistics:: [{}]", userStats);
		
		return userStats;
	}
}
