package uq.deco2800.singularity.clients.ducktales;

import java.io.IOException;
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
import uq.deco2800.singularity.common.representations.Trade;
import uq.deco2800.singularity.common.representations.TradeRequest;
import uq.deco2800.singularity.common.representations.TradeResponse;
import uq.deco2800.singularity.common.representations.User;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;




public class DucktalesClient extends SingularityRestClient {
	 private static final String CLASS = DucktalesClient.class.getName();
	    
	    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	    
	    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

		private Token token;

	    public DucktalesClient()
	    {
	    	super();
	    }
	    
	    public DucktalesClient(String host, int port) {
	        super(host, port);
	    }
	
	   /**
	    * Get the trades by the collection Id
	    * 
	    *@param collectionId
	    *
	    *@return the current trades
	    * 
	    */
	    public Trade getCurrentTrade(String collectionId) {
	    	LOGGER.info("Attempting to obtain current trades", collectionId);
	    	
	    	UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.TRADE_RESOURCE)
	                .queryParam("collectionId", collectionId);
	        
	        Response response = client.target(uriBuilder).request().get();
	        
	        if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
	            throw new WebApplicationException(response);
	        }
	        
	        List<Trade> tradeCollection = response.readEntity(new GenericType<List<Trade>>() {});
	        
	        Trade finalCurrentTrade = tradeCollection.get(0);
	        
	        return finalCurrentTrade;
	    }
	    
	    /**
	     * Creating a trade 
	     * 
	     * @param collectionId
	     * collectionId to filter by
	     * @param requestId
	     * requestId to filter by 
	     * @return the current trades
	     * @throws JsonProcessingException 
	     * 
	     */
	    public TradeRequest createTrade(TradeRequest trade) throws WebApplicationException, JsonProcessingException  {

	        LOGGER.info("Attempting to create trade: [{}]", trade);
	        
	        // Update token if needed. 
	        this.token = renewIfNeededAndGetToken();
	        
	        UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.TRADEREQUEST_RESOURCE).path(ServerConstants.NEW)
	                .queryParam("token", token.getTokenId());
	        
	        String data = MAPPER.writeValueAsString(trade);
	        
	        Response response = client.target(uriBuilder).request().post(Entity.json(data));
	        
	        if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
	            throw new WebApplicationException(response);
	        }
	        
	        String requestId = response.readEntity(String.class);
	        trade.setRequestId(requestId);
	        LOGGER.info("Successfully created a new trade with tradeId: [{}]", requestId);
	        
	        return trade;
	    }
	    
	    
	    public List<TradeRequest> getTradesMadeByUser(User user) throws IOException {
	    	LOGGER.info("Attempting to retrieve trade requests made by : [{}]", user.getUsername());
	    	
	    	UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.TRADEREQUEST_RESOURCE).path(ServerConstants.USER_RESOURCE);
	    	
	    	String userData = MAPPER.writeValueAsString(user);
	    	
	    	Response response = client.target(uriBuilder).request().post(Entity.json(userData));
	    	
	    	if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
				throw new WebApplicationException(response);
			}
	    	
	    	String data = response.readEntity(String.class);
	    	List<TradeRequest> trades = MAPPER.readValue(data,
					MAPPER.getTypeFactory().constructCollectionType(List.class, TradeRequest.class));
	    	LOGGER.info("TradeRequests for userId {}: {}", user.getUserId(), trades);
			return trades;
	    	
	    }
	    
	    public List<TradeRequest> getTradesNotMadeByUser(User user) throws IOException {
	    	LOGGER.info("Attempting to retrieve trade requests not made by : [{}]", user.getUsername());
	    	
	    	UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.TRADEREQUEST_RESOURCE).path(ServerConstants.OTHER_RESOURCE);
	    	
	    	String userData = MAPPER.writeValueAsString(user);
	    	
	    	Response response = client.target(uriBuilder).request().post(Entity.json(userData));
	    	
	    	if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
				throw new WebApplicationException(response);
			}
	    	
	    	String data = response.readEntity(String.class);
	    	List<TradeRequest> trades = MAPPER.readValue(data,
					MAPPER.getTypeFactory().constructCollectionType(List.class, TradeRequest.class));
	    	LOGGER.info("TradeRequests by users other than the userId {}: {}", user.getUserId(), trades);
			return trades;
	    	
	    }
	    
	    

	    /**
	     * Creating a new trade Response 
	     * 
	     *@param userId
	     * collectionId to filter by
	     * @return the current trades
	     * @throws JsonProcessingException 
	     * 
	     */
	    
	    public TradeResponse createReponse(TradeResponse tradeResponse) throws WebApplicationException, JsonProcessingException  {

	        LOGGER.info("Attempting to create a trade Response: [{}]", tradeResponse);
	        
	        // Update token if needed. 
	        this.token = renewIfNeededAndGetToken();
	        
	        UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.TRADEREQUEST_RESOURCE).path(ServerConstants.NEW)
	                .queryParam("token", token.getTokenId());
	        
	        String data = MAPPER.writeValueAsString(tradeResponse);
	        
	        System.out.println(tradeResponse);
	        
	        Response response = client.target(uriBuilder).request().post(Entity.json(data));
	        
	        
	        if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
	            throw new WebApplicationException(response);
	        }
	        
	        String itemId = response.readEntity(String.class);
	        String collectionId = response.readEntity(String.class);
	        String userId = response.readEntity(String.class);;
	        tradeResponse.setcollectionId(collectionId);
	        tradeResponse.setitemId(itemId);
	        tradeResponse.setUserId(userId);
	        LOGGER.info("Successfully create a new tradeResponse: [{}]", tradeResponse);
	        
	        return tradeResponse;
	    }

	    /**
		    * Get the trades by the collection Id
		    * 
		    *@param collectionId
		    *
		    *@return the current trades
		    * 
		    */
		    public TradeResponse getTradeResponse(String userId) {
		    	LOGGER.info("Attempting to obtain current trades", userId);
		    	
		    	UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.TRADE_RESOURCE)
		                .queryParam("collectionId", userId);
		        
		        Response response = client.target(uriBuilder).request().get();
		        
		        if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
		            throw new WebApplicationException(response);
		        }
		        
		        List<TradeResponse> tradeCollection = response.readEntity(new GenericType<List<TradeResponse>>() {});
		        
		        
		        TradeResponse finalCurrentTrade = tradeCollection.get(0);
		        
		        return finalCurrentTrade;
		    }
/**
 * 
 * Getting a trade through a collection Id
 * 
 * @param collectionId
 * @return
 * @throws WebApplicationException
 */
		    
public Trade getTradeInformationById(String collectionId) throws WebApplicationException {
	LOGGER.info("Attempting to retrieve information about: [{}]", collectionId);
		// No need to renew token as authentication is not needed for this
	UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.TRADE_RESOURCE).queryParam("collectionId", collectionId);
				Response response = client.target(uriBuilder).request().get();
				if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
					throw new WebApplicationException(response);
				}
				Trade trade = response.readEntity(Trade.class);
				LOGGER.info("Successfully retrieved information: [{}]", trade);
				return trade;
			}
/**
 * Create a new collection through the client 
 * @param trade
 * @return
 * @throws JsonProcessingException
 * @throws WebApplicationException
 */
public Trade createCollection(Trade trade) throws JsonProcessingException, WebApplicationException {
	LOGGER.info("Attempting to create trade: [{}]", trade);
	UriBuilder uriBuilder = rootUriBuilder().path(ServerConstants.TRADE_RESOURCE).path(ServerConstants.NEW);
	String data = MAPPER.writeValueAsString(trade);
	Response response = client.target(uriBuilder).request().post(Entity.json(data));
	if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
		throw new WebApplicationException(response);
	}
	String tradeId = response.readEntity(String.class);
	trade.setcollectionId(tradeId);
	LOGGER.info("Successfully created trade with trade ID: [{}]", tradeId);
	return trade;
}  
}
