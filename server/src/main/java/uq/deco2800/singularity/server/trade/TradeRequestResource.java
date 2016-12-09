package uq.deco2800.singularity.server.trade;

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

import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.representations.TradeRequest;
import uq.deco2800.singularity.common.representations.User;



/**
 * 
 * Resource to manage trade requests 
 * @author Gregory
 *
 */
@Path(ServerConstants.TRADEREQUEST_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class TradeRequestResource {
	
	private static final String CLASS = TradeRequestResource.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	private static final ObjectMapper MAPPER = new ObjectMapper();
	// The Data Access object to interface with the User database.
	private final TradeRequestDao tradeRequestDao;

	/**
	 * Constructor for this resource to set up the DAO.
	 * 
	 * @param tradeRequestDao
	 */
	public TradeRequestResource(TradeRequestDao tradeRequestDao) {
		if (tradeRequestDao == null) {
			throw new NullPointerException(
					"Parameter (tradeRequestDao) must not be null");
		}
		this.tradeRequestDao = tradeRequestDao;
	}

	/**
	 * Retrieves all information known about a trade request.
	 * 
	 * @param requestId
	 * @return
	 */
	@GET
	public TradeRequest getTradeRequestId(
			@QueryParam("requestId") Optional<String> requestId,
			@QueryParam("offeredCollectionId") Optional<String>
			offeredCollectionId) {
		if (requestId.isPresent() && !requestId.get().isEmpty()) {
			TradeRequest tradeRequest = tradeRequestDao.findByRequestId
					(requestId.get());
			if (tradeRequest != null) {
				return tradeRequest;
			}
			throw new WebApplicationException(
					"Trade Request not found for given requestId", 
					Status.NOT_FOUND);
		} else if (offeredCollectionId.isPresent() && !offeredCollectionId.get()
				.isEmpty()) {
			TradeRequest tradeRequest = tradeRequestDao.findByCId
					(offeredCollectionId.get());
			if (tradeRequest != null) {
				return tradeRequest;
			}
			throw new WebApplicationException(
					"Trade Request not found for given CId", Status.NOT_FOUND);
		} else {
			throw new WebApplicationException(
					"RequestId or CId required as query parameter",
					Status.BAD_REQUEST);
		}
	}
	
	
	@POST
	@Path(ServerConstants.USER_RESOURCE)
	public List<TradeRequest> getTradesByUserId(String payload) throws IOException {
		
		User user = null;
		try {
			user = MAPPER.readValue(payload, User.class);
		} catch (IOException e) {
			throw e;
		}
		
		return tradeRequestDao.findByUId(user.getUserId());
	}
	
	
	@POST
	@Path(ServerConstants.OTHER_RESOURCE)
	public List<TradeRequest> getTradesNotByUserId(String payload) throws IOException {
		
		User user = null;
		try {
			user = MAPPER.readValue(payload, User.class);
		} catch (IOException e) {
			throw e;
		}
		
		return tradeRequestDao.findAndExcludeUId(user.getUserId());
		
	}

	/**
	 * Creates a new trade request in the system if there is no missing 
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
		TradeRequest newTradeRequest = null;
		try {
			newTradeRequest = MAPPER.readValue(payload, TradeRequest.class);
		} catch (JsonParseException | JsonMappingException e) {
			throw new WebApplicationException("Malformed request",
					Status.BAD_REQUEST);
		}
		try {
			getTradeRequestId(Optional.fromNullable(
					newTradeRequest.getOfferedCollectionId()), 
					Optional.absent());
			throw new WebApplicationException("getOfferedCollectionId already " 
					+ "exists",
					Status.CONFLICT);
		} catch (WebApplicationException exception) {
			if (exception.getResponse().getStatus() != Status.NOT_FOUND
					.getStatusCode()) {
				throw exception;
			}
		}
		
		// Create a new random UUID for the trade request
		String requestId = UUID.randomUUID().toString();
		
		//TODO: Check that the given offeredID is already in the offers table
		//      and if not, throw a bad request.
		// The offeredCollectionId should already be in the collection table
		
		
		while (tradeRequestDao.findByRequestId(requestId) != null) {
			requestId = UUID.randomUUID().toString();
		}
		
		// Unique requestId and offeredCollectionId have been found.
		
		newTradeRequest.setRequestId(requestId);		
		tradeRequestDao.insert(newTradeRequest);
		return Response.status(Status.CREATED).entity(requestId).build();
	}


}
