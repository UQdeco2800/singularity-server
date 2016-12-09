package uq.deco2800.singularity.server.achievement;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.duxcom.Achievement;

/**
 * Resource used to manage a achievement.
 *
 * @author Daniel Gormly.
 */
@Path(ServerConstants.USER_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class AchievementResource {

    private static final String CLASS = AchievementResource.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    // The Data Access object to interface with the Achievement database.
    private final AchievementDao achievementDao;

    /**
     * Constructor for this resource to set up the DAO.
     *
     * @param achievementDao
     */
    public AchievementResource(AchievementDao achievementDao) {
        if (achievementDao == null) {
            throw new NullPointerException(
                    "Parameter (achievementDao) must not be null");
        }
        this.achievementDao = achievementDao;
    }

    /**
     * Retrieves all information known about a achievement from a given name.
     *
     * @param name
     * @return
     */
    @GET
    public Achievement getAchievementInformation(
            @QueryParam("name") Optional<String> name,
            @QueryParam("Id") Optional<String> achievementId) {
        if (name.isPresent() && !name.get().isEmpty()) {
            Achievement achievement = achievementDao.findByName(name.get());
            if (achievement != null) {
                return achievement;
            }
            throw new WebApplicationException(
                    "Achievement not found for given name", Status.NOT_FOUND);
        } else if (achievementId.isPresent() && !achievementId.get().isEmpty()) {
            Achievement achievement = achievementDao.findById(achievementId.get());
            if (achievement != null) {
                return achievement;
            }
            throw new WebApplicationException(
                    "Achievement not found for given achievement id", Status.NOT_FOUND);
        } else {
            throw new WebApplicationException(
                    "Name or Id required as query parameter", Status.BAD_REQUEST);
        }
    }

    /**
     * Gets all Achievements in the database.
     *
     * @return List<Achievement>, Null if none in table.
     */
    @GET
    public List<Achievement> getAllAchievementInformation() {
        return achievementDao.getAll();
    }

    /**
     * Creates a new achievement in the system if there is no missing information.
     *
     * @param payload
     *            The data sent in the request
     * @return 201 Created if the achievement was created.
     */
    @POST
    @Path(ServerConstants.NEW)
    public Response createNewAchievement(Achievement payload) throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException {
        Achievement newAchievement;
        newAchievement = payload;
        achievementDao.insert(newAchievement);
        return Response.status(Status.CREATED).entity(newAchievement).build();
    }

}
