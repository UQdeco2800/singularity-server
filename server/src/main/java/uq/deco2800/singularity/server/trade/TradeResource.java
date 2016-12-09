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
import uq.deco2800.singularity.common.representations.Trade;
import uq.deco2800.singularity.server.trade.TradeDao;

/**
 * Resource used to manage a Trade.
 * 
 *
 */
@Path(ServerConstants.TRADE_RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class TradeResource {

	private static final String CLASS = TradeResource.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	private static final ObjectMapper MAPPER = new ObjectMapper();
	// The Data Access object to interface with the User database.
	private final TradeDao tradeDao;

	/**
	 * Constructor for this resource to set up the DAO.
	 * 
	 * @param tradeDao
	 */
	public TradeResource(TradeDao tradeDao) {
		if (tradeDao == null) {
			throw new NullPointerException(
					"Parameter (tradeDoa) must not be null");
		}
		this.tradeDao = tradeDao;
	}

	/**
	 * Retrieves all information known about a collection from a given collection
	 * Id.
	 * 
	 * @param itemId
	 * @return
	 */
	@GET
	public Trade getCollectionInformation(
			@QueryParam("collectionId") Optional<String> collectionId) {
		if (collectionId.isPresent() && !collectionId.get().isEmpty()) {
			Trade trade = tradeDao.findById(collectionId.get());
			if (trade != null) {
				return trade;
			}
			throw new WebApplicationException(
					"Trade not found for given collection Id", Status.NOT_FOUND);
		} else {
			throw new WebApplicationException(
					"Colelction ID required as query parameter",
					Status.BAD_REQUEST);
		}
	}

	/**
	 * Creates a new collection in the system if there is no missing information.
	 * 
	 * @param payload
	 *            The data sent in the request
	 * @return 201 Created if the trade was created.
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	@POST
	@Path(ServerConstants.NEW)
	public Response createNewCollection(String payload) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		Trade newTrade = null;
		try {
			newTrade = MAPPER.readValue(payload,Trade.class);
		} catch (JsonParseException | JsonMappingException e) {
			throw new WebApplicationException("Malformed request",
					Status.BAD_REQUEST);
		}
		try {
			getCollectionInformation(Optional.fromNullable(newTrade.getcollectionId()));
			throw new WebApplicationException("Collection Id already exists",
					Status.CONFLICT);
		} catch (WebApplicationException exception) {
			if (exception.getResponse().getStatus() != Status.NOT_FOUND
					.getStatusCode()) {
				throw exception;
			}
		}
		String collectionId = UUID.randomUUID().toString();
		while (tradeDao.findById(collectionId) != null) {
			collectionId = UUID.randomUUID().toString();
		}
		newTrade.setcollectionId(collectionId);
		tradeDao.insert(newTrade);
		return Response.status(Status.CREATED).entity(collectionId).build();
	}

	
}
