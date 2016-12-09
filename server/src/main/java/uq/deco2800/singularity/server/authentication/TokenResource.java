package uq.deco2800.singularity.server.authentication;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.UUID;

import javax.ws.rs.GET;
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

import io.dropwizard.jersey.setup.JerseyEnvironment;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.User;
import uq.deco2800.singularity.common.util.PasswordHashUtils;
import uq.deco2800.singularity.server.user.UserDao;

/**
 * A resource to handle all token requests sent to the server. Used in conjunction with the {@link JerseyEnvironment}
 * provided by Dropwizard. This class should be registered with the JerseyEnvironment so that the JerseyEnvironment
 * knows of the existence of this class and which URLs should be forwarded to this class.
 * 
 * @author dion-loetscher
 *
 */
@Path(ServerConstants.AUTHENTICATION_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class TokenResource {

	/**
	 * A String representation of this class. Primarily used for Logging.
	 */
	private static final String CLASS = TokenResource.class.getName();

	/**
	 * {@link Logger} from SL4J used to log at different levels.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	/**
	 * The DataAccessObject for all tokens. Used to find, insert and delete tokens.
	 */
	private final TokenDao tokenDao;

	/**
	 * The DataAccessObject for all users. Used to find users and their hashed passwords so that the username/password
	 * combination can be verified before a token is generated for a user.
	 */
	private final UserDao userDao;

	/**
	 * Constructor for this resource. In order to instantiate this class, a UserDao and TokenDao connected to the
	 * database are required.
	 * 
	 * @param tokenDao
	 *            A valid {@link TokenDao} which has been connected to a database.
	 * @param userDao
	 *            A valid {@link UserDao} which has been connected to a database.
	 * 
	 * @throws NullPointerException
	 *             Thrown if either the tokenDao or userDao or null
	 */
	public TokenResource(TokenDao tokenDao, UserDao userDao) {
		if (tokenDao == null || userDao == null) {
			throw new NullPointerException("Parameters (tokenDao, userDao) must not be null");
		}
		this.tokenDao = tokenDao;
		this.userDao = userDao;
	}

	/**
	 * Responds to the <em>{@link ServerConstants#NEW}</em> sub-path of this resource allowing a user to attempt to
	 * retrieve a new token by providing a username and password. The username and password are bound to the Query
	 * Parameters of the HTTP request (i.e. <em>?key1=value1&key2=value2&etc=...</em>).
	 * 
	 * @param username
	 *            The username of the user to be authenticated. {@code username.isPresent()} should return true and
	 *            {@code username.get().isEmpty()} should return false
	 * @param password
	 *            The password of the user to be authenticated. {@code password.isPresent()} should return true and
	 *            {@code password.get().isEmpty()} should return false
	 * @return A {@link Response} object which encapsulates a {@link Status#OK} and the new {@link Token} object
	 * 
	 * @throws WebApplicationException
	 *             Thrown if either there is no username or password ({@link Status#BAD_REQUEST}), or if the username
	 *             and password do not correctly authenticate a user ({@link Status#FORBIDDEN}).
	 */
	@GET
	@Path(ServerConstants.NEW)
	public Response newToken(@QueryParam("username") Optional<String> username,
			@QueryParam("password") Optional<String> password) {
		if (!username.isPresent() || !password.isPresent()) {
			throw new WebApplicationException("Both username and password are required as query parameters",
					Status.BAD_REQUEST);
		}
		User user = userDao.findByUsername(username.get());
		if (user == null) {
			throw new WebApplicationException("Username or password is incorrect", Status.FORBIDDEN);
		}
		boolean validated = false;
		try {
			validated = PasswordHashUtils.validatePassword(password.get(), user.getPassword(), user.getSalt());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			LOGGER.error("Could not validate password when making token", e);
		}
		if (validated) {
			tokenDao.deleteByUserId(user.getUserId());
			Token token = createToken(user.getUserId());
			return Response.ok(token).build();
		} else {
			throw new WebApplicationException("Username or password is incorrect", Status.FORBIDDEN);
		}
	}

	/**
	 * Responds to the <em>{@link ServerConstants#RENEW}</em> sub-path of this resource allowing a user to attempt to
	 * retrieve a new token by providing valid (not yet expired) token. The token is bound to the Query Parameters of
	 * the HTTP request (i.e. <em>?key1=value1&key2=value2&etc=...</em>).
	 * 
	 * @param token
	 *            The valid token of the user to be authenticated. {@code token.isPresent()} should return true and
	 *            {@code token.get().isEmpty()} should return false. Finally, the token should be a string
	 *            representation of a UUID.
	 * @return A {@link Response} object which encapsulates a {@link Status#OK} and the new {@link Token} object
	 * 
	 * @throws WebApplicationException
	 *             Thrown if either there is no token ({@link Status#BAD_REQUEST}), or if the token is an invalid or
	 *             expired token ({@link Status#FORBIDDEN}).
	 */
	@GET
	@Path(ServerConstants.RENEW)
	public Response renewToken(@QueryParam("token") Optional<String> token) {
		if (!token.isPresent()) {
			throw new WebApplicationException("Token is required as a query parameter to renew", Status.BAD_REQUEST);
		}
		String currentTokenId = token.get();
		Token currentToken = tokenDao.findByTokenId(currentTokenId);
		if (currentToken == null || currentToken.getExpires() <= System.currentTimeMillis()) {
			throw new WebApplicationException("Token is invalid or expired", Status.FORBIDDEN);
		}
		Token newToken = createToken(currentToken.getUserId());
		tokenDao.deleteByTokenId(currentTokenId);

		return Response.ok(newToken).build();
	}

	/**
	 * Private helper method used to create a token from a given user ID. Handles the task of generating a new token and
	 * ensuring that it doesn't collide with an existing token ID stored in the database. <br>
	 * <br>
	 * N.B. Theoretically should not occur often, but may want to put a try catch around the insert to that if an
	 * exception does occur to to a non-unique primary key (which could occur if another thread generates the same token
	 * between the time this method call generated the token ID and the time it was inserted).
	 * 
	 * @param userId
	 *            A String representation of a UUID of a user ID.
	 * @return A token which is valid and has been inserted into the database.
	 */
	private Token createToken(String userId) {
		Token token = new Token(userId);
		while (tokenDao.findByTokenId(token.getTokenId()) != null) {
			LOGGER.trace("Generated token ID which already exists. " + "Generating a new one");
			token.setTokenId(UUID.randomUUID().toString());
		}
		tokenDao.insert(token);
		return token;
	}
}
