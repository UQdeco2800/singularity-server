package uq.deco2800.singularity.clients.duxcom;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.duxcom.PlayerStats;

public class DuxcomClient extends SingularityRestClient{

    private static final String CLASS = DuxcomClient.class.getName();
    
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

    private Token token;
    
    public DuxcomClient() {
        super();
    }
    
    public DuxcomClient(String host, int port) {
        super(host, port);
    }
    
    /**
     * Get the current score of a player given their userId
     * 
     * @param userId
     *          userId to filter by
     * @return the current playerStats
     */
    public PlayerStats getCurrentScore(String userId) {
        LOGGER.info("Attempting to retrieve current score of userId: [{}]", userId);
        
        // Update token if needed. 
        this.token = renewIfNeededAndGetToken();
        
        UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.PLAYER_STATS_RESOURCE)
                .queryParam("token", token.getTokenId())
                .queryParam("userId", userId);
        
        Response response = client.target(uriBuilder).request().get();
        
        if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
            throw new WebApplicationException(response);
        }
        
        List<PlayerStats> playerStats = response.readEntity(new GenericType<List<PlayerStats>>() {});
        
        PlayerStats finalPlayerStats = playerStats.get(0);
        
        return finalPlayerStats;
        
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public List<PlayerStats> getHighscoresByType(String type) {
        LOGGER.info("Attempting to retrieve current score of userId: [{}]", type);
        
        // Update token if needed. 
        this.token = renewIfNeededAndGetToken();
        
        UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.PLAYER_STATS_RESOURCE)
                .queryParam("token", token.getTokenId())
                .queryParam("type", type);
        
        Response response = client.target(uriBuilder).request().get();
        
        if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
            throw new WebApplicationException(response);
        }
        
        List<PlayerStats> playerStats = response.readEntity(new GenericType<List<PlayerStats>>() {});
        
        return playerStats;
        
    }
    
    
    /**
     * create player stats
     * 
     * @param userId
     *          userId to filter by
     * @return the current playerStats
     * @throws JsonProcessingException 
     */
    public PlayerStats createPlayerStats(PlayerStats playerStats) throws WebApplicationException, JsonProcessingException  {

        LOGGER.info("Attempting to submit Player Stats: [{}]", playerStats);
        
        // Update token if needed. 
        this.token = renewIfNeededAndGetToken();
        
        UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.PLAYER_STATS_RESOURCE).path(ServerConstants.NEW)
                .queryParam("token", token.getTokenId());
        
        String data = MAPPER.writeValueAsString(playerStats);
        
        System.out.println(playerStats);
        
        Response response = client.target(uriBuilder).request().post(Entity.json(data));
        
        
        if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
            throw new WebApplicationException(response);
        }
        
        String scoreId = response.readEntity(String.class);
        playerStats.setScoreId(scoreId);
        LOGGER.info("Successfully created score: [{}]", playerStats);
        
        return playerStats;
    }


    
    
    
    
}
