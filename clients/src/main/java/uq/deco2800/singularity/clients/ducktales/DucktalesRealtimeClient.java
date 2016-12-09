package uq.deco2800.singularity.clients.ducktales;

import java.io.IOException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Client;

import uq.deco2800.singularity.clients.realtime.RealTimeClient;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.TradeRequest;
import uq.deco2800.singularity.common.representations.TradeResponse;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;



public class DucktalesRealtimeClient extends RealTimeClient{
	
	private static final String CLASS = DucktalesRealtimeClient.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);
    private boolean willReceiveRequests = false;
    private boolean willReceiveResponse = true;
    protected SingularityRestClient restClient;
	protected Client realTimeClient;
	
	private SessionType gameType;
    
	/**
	 * @param configuration
	 * @param restClient
	 * @return 
	 * @throws IOException
	 */
	public DucktalesRealtimeClient(RealTimeSessionConfiguration configuration, SingularityRestClient client,
			SessionType gameType) throws IOException {
		super(configuration, client);
		this.gameType = gameType;
		register();
	}
	/**
	 * Setting the real time client to register a trade request 
	 * 
	 */
	public void RegisterTradeRequest(String requestId, String offeredCollectionId) {
		if (!willReceiveRequests) {
			LOGGER.warn("Registration has not been successful - User may not "
					+ "Trade Requests");
		}
		TradeRequest registrationAttempt = new TradeRequest();
		registrationAttempt.setRequestId(requestId);
		registrationAttempt.setOfferedCollectionId(offeredCollectionId);
		realTimeClient.sendTCP(registrationAttempt);
		LOGGER.info("Sending Trade Request: [{}]", registrationAttempt);
	}
	
	/**
	 * Setting the real time client to register a trade response
	 * 
	 */
	public void RegisterTradeResponse(String itemId, String collectionId) {
		if (!willReceiveResponse) {
			LOGGER.warn("Registration has not been successful - User may not "
					+ "Trade Response");
		}
		TradeResponse registrationAttempt = new TradeResponse();
		Token token = restClient.renewIfNeededAndGetToken();
		registrationAttempt.setUserId(token.getUserId());
		registrationAttempt.setitemId(itemId);
		registrationAttempt.setcollectionId(collectionId);
		realTimeClient.sendTCP(registrationAttempt);
		LOGGER.info("Sending Trade Response: [{}]", registrationAttempt);
	}
	
	/**
	 * 
	 * @return
	 */
	public SessionType getSessionType() {
		return gameType;
	}

}
