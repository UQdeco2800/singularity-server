package uq.deco2800.singularity.server.trade;

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
import uq.deco2800.singularity.common.representations.TradeResponse;
import uq.deco2800.singularity.server.authentication.TokenDao;
import uq.deco2800.singularity.server.trade.TradeResponseDao;
import uq.deco2800.singularity.server.user.UserDao;



/**
 * 
 * Resource to manage trade response
 * @author Gregory
 *
 */
@Path(ServerConstants.TRADERESPONSE_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class TradeResponseResource {
	
	private static final String CLASS = TradeResponseResource.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	private static final ObjectMapper MAPPER = new ObjectMapper();
	// The Data Access object to interface with the trade response database.
	private final TradeResponseDao tradeResponseDao;

	/**
	 * Constructor for this resource to set up the DAO.
	 * 
	 * @param tradeResponseDao
	 * @param tokenDao 
	 * @param userDao 
	 */
	public TradeResponseResource(TradeResponseDao tradeResponseDao, UserDao userDao, TokenDao tokenDao) {
		if (tradeResponseDao == null) {
			throw new NullPointerException(
					"Parameter (tradeResponseDao) must not be null");
		}
		this.tradeResponseDao = tradeResponseDao;
	}

	/**
	 * Retrieves all information known about a trade response.
	 * 
	 * @param user Id
	 * @return
	 */
	@GET
	public TradeResponse getTradeResponseId(
			@QueryParam("userId") Optional<String> userId,
			@QueryParam("CollectionId") Optional<String>
			CollectionId){
		if (userId.isPresent() && !userId.get().isEmpty()) {
			TradeResponse tradeResponse = tradeResponseDao.findById
					(userId.get());
			throw new WebApplicationException(
					"Trade Response not found for given userId", 
					Status.NOT_FOUND);
		
		} else {
			throw new WebApplicationException(
					"User id  required as query parameter",
					Status.BAD_REQUEST);
		}
	}

	/**
	 * Creates a new trade response in the system if there is no missing 
	 * information.
	 * 
	 * @param payload
	 *            The data sent in the request
	 * @return 201 Created if the trade request was created.
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	@POST
	@Path(ServerConstants.NEW)
	public Response createNewTradeRequest(String payload) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		TradeResponse newTradeResponse = null;
		try {
			newTradeResponse = MAPPER.readValue(payload, TradeResponse.class);
		} catch (JsonParseException | JsonMappingException e) {
			throw new WebApplicationException("Malformed request",
					Status.BAD_REQUEST);
		}
		try {
			getTradeResponseId(Optional.fromNullable(newTradeResponse.getUserId
					()), Optional.absent());
			throw new WebApplicationException("Request Id already exists",
					Status.CONFLICT);
		} catch (WebApplicationException exception) {
			if (exception.getResponse().getStatus() != Status.NOT_FOUND
					.getStatusCode()) {
				throw exception;
			}
		}
		String userId = UUID.randomUUID().toString();
		while (tradeResponseDao.findById(userId) != null) {
			userId = UUID.randomUUID().toString();
		}
		newTradeResponse.setUserId(userId);
		tradeResponseDao.insert(newTradeResponse);
		return Response.status(Status.CREATED).entity(userId).build();
	}


}
