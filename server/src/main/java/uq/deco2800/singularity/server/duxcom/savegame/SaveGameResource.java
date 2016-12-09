package uq.deco2800.singularity.server.duxcom.savegame;

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
import uq.deco2800.singularity.common.representations.duxcom.PlayerStats;
import uq.deco2800.singularity.server.authentication.TokenDao;
import uq.deco2800.singularity.server.user.UserDao;
import uq.deco2800.singularity.server.user.UserResource;    

/**
 * Resource used to manage Duxcom save game.
 * 
 * @author jhess-osum
 *
 */
@Path(ServerConstants.PLAYER_STATS_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class SaveGameResource {
    private static final String CLASS = UserResource.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    
    // The Data Access object to interface with the User database.
    private final SaveGameDao saveGameDao;
    private final UserDao userDao;
    private final TokenDao tokenDao;

    /**
     * Constructor for this resource to set up the dao
     * 
     * @param saveGameDao
     * @param userDao
     * @param tokenDao
     */
    public SaveGameResource(SaveGameDao saveGameDao, UserDao userDao, TokenDao tokenDao){
        
        if (saveGameDao == null) {
            throw new NullPointerException("Parameter (saveGameDao) must not be null");
        }
        if (userDao == null) {
            throw new NullPointerException("Parameter (userDao) must not be null");
        }
        if (tokenDao == null) {
            throw new NullPointerException("Parameter (userDao) must not be null");
        }
        
        this.userDao = userDao;
        this.saveGameDao = saveGameDao;
        this.tokenDao = tokenDao;
    }
    
    /**
     *  Retrieves the top 10 high scores by type
     */
    @GET
    public List<PlayerStats> getTypeHighscores(
            @QueryParam("type") Optional<String> type,
            @QueryParam("token") Optional<String> tokenId){
        
        if (!tokenId.isPresent() || tokenId.get().isEmpty()) {
            throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
        }
        
        Token token = tokenDao.findByTokenId(tokenId.get());
        if (token == null || token.getExpires() <= System.currentTimeMillis()) {
            throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
        }
        
        List<PlayerStats> playerStats = null;
        
        
        if (type.isPresent()) {
            switch (type.get()){
            case "SCORE":
                playerStats = saveGameDao.findScoreHighScores();
            case "KILLS":
                playerStats = saveGameDao.findKillsHighScores();
            }}
        if (playerStats != null) {
            return playerStats;
        } else {
            throw new WebApplicationException(
                    "Scores could not be found for type: " + type, Status.NOT_FOUND);
        } 

    }
    
 
    
    /**
     * Inserts a new record into the PlayerStats table in the database.
     * If there is no missing information
     * 
     * @param payload
     * @param tokenId
     * @return 201 Created if the user was created.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @POST
    @Path(ServerConstants.NEW)
    public Response createNewPlayerStats(String payload, @QueryParam("token") Optional<String> tokenId) throws IOException,
    NoSuchAlgorithmException, InvalidKeySpecException {
        
    if (!tokenId.isPresent() || tokenId.get().isEmpty()) {
        throw new WebApplicationException("A token is required as a query parameter", Status.BAD_REQUEST);
    }

    Token token = tokenDao.findByTokenId(tokenId.get());
    
    if (token == null || token.getExpires() <= System.currentTimeMillis()) {
        throw new WebApplicationException("The token is invalid or has expired", Status.FORBIDDEN);
    }
    
    PlayerStats newPlayerStats = null;
    
    
    // make new player stats
    try {
        newPlayerStats = MAPPER.readValue(payload, PlayerStats.class);
        
    } catch (JsonParseException | JsonMappingException e) {
        throw new WebApplicationException("Malformed request",
                Status.BAD_REQUEST);
    }
    
    //make sure user is in the system
    try {
        userDao.findById(newPlayerStats.getUserId());
    } catch (Exception e ) {
        throw new WebApplicationException("User doesn't exist",
                Status.NOT_FOUND);
    }
    
    String scoreId = UUID.randomUUID().toString();
    
    while (saveGameDao.findByScoreId(scoreId) != null) {
        scoreId = UUID.randomUUID().toString();
    }
    
    //Set new ID
    newPlayerStats.setScoreId(scoreId);
    
    if (Integer.parseInt(newPlayerStats.getScore()) <= 0) {
        throw new WebApplicationException("Score cannot be negative or zero.", Status.BAD_REQUEST);
    }
    
    //add the new user
    saveGameDao.insert(newPlayerStats);
    
    //return  player stats
     return Response.status(Status.CREATED).entity(scoreId).build();
    }
    
}
