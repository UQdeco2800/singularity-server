package uq.deco2800.singularity.server.user;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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

import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.User;
import uq.deco2800.singularity.common.util.PasswordHashUtils;

/**
 * Resource used to manage a user.
 * 
 * @author dloetscher
 *
 */
@Path(ServerConstants.USER_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private static final String CLASS = UserResource.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	private static final ObjectMapper MAPPER = new ObjectMapper();
	// The Data Access object to interface with the User database.
	private final UserDao userDao;

	/**
	 * Constructor for this resource to set up the DAO.
	 * 
	 * @param userDao
	 */
	public UserResource(UserDao userDao) {
		if (userDao == null) {
			throw new NullPointerException(
					"Parameter (userDao) must not be null");
		}
		this.userDao = userDao;
	}

	/**
	 * Retrieves all information known about a user from a given username.
	 * 
	 * @param username
	 * @return
	 */
	@GET
	public User getUserInformation(
			@QueryParam("username") Optional<String> username,
			@QueryParam("userId") Optional<String> userId) {
		if (username.isPresent() && !username.get().isEmpty()) {
			User user = userDao.findByUsername(username.get());
			if (user != null) {
				user.clearPasswords();
				return user;
			}
			throw new WebApplicationException(
					"User not found for given username", Status.NOT_FOUND);
		} else if (userId.isPresent() && !userId.get().isEmpty()) {
			User user = userDao.findById(userId.get());
			if (user != null) {
				user.clearPasswords();
				return user;
			}
			throw new WebApplicationException(
					"User not found for given user id", Status.NOT_FOUND);
		} else {
			throw new WebApplicationException(
					"Username or userId required as query parameter", Status.BAD_REQUEST);
		}
	}

	/**
	 * Creates a new user in the system if there is no missing information.
	 * 
	 * @param payload
	 *            The data sent in the request
	 * @return 201 Created if the user was created.
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	@POST
	@Path(ServerConstants.NEW)
	public Response createNewUser(String payload) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		User newUser = null;
		try {
			newUser = MAPPER.readValue(payload, User.class);
		} catch (JsonParseException | JsonMappingException e) {
			throw new WebApplicationException("Malformed request",
					Status.BAD_REQUEST);
		}
		try {
			getUserInformation(Optional.fromNullable(newUser.getUsername()), Optional.absent());
			throw new WebApplicationException("Username already exists",
					Status.CONFLICT);
		} catch (WebApplicationException exception) {
			if (exception.getResponse().getStatus() != Status.NOT_FOUND
					.getStatusCode()) {
				throw exception;
			}
		}
		String userId = UUID.randomUUID().toString();
		while (userDao.findById(userId) != null) {
			userId = UUID.randomUUID().toString();
		}
		newUser.setUserId(userId);
		hashPassword(newUser);
		userDao.insert(newUser);
		return Response.status(Status.CREATED).entity(userId).build();
	}

	/**
	 * Takes a User which has an unhashed password and updates its fields to a
	 * hashed password.
	 * 
	 * @param user
	 *            A non null user object which has a password which has not been
	 *            hashed.
	 * @throws NoSuchAlgorithmException
	 *             Thrown for the reasons specified in
	 *             {@link #PasswordHashUtils}
	 * @throws InvalidKeySpecException
	 *             Thrown for the reasons specified in
	 *             {@link #PasswordHashUtils}
	 */
	private void hashPassword(User user)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String password = user.getPassword();
		byte[] salt = PasswordHashUtils.getSalt();
		user.setSalt(PasswordHashUtils.toHexString(salt));
		user.setPassword(PasswordHashUtils.hash(password, salt));
	}
}
