package uq.deco2800.singularity.clients.realtime;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.clients.realtime.messaging.MessagingClient;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;
import uq.deco2800.singularity.common.representations.realtime.Registration;
import uq.deco2800.singularity.common.util.KryoUtils;

import java.io.IOException;

/**
 * @author dloetscher
 */
public abstract class RealTimeClient {

	private static final String CLASS = MessagingClient.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	protected SingularityRestClient restClient;
	protected Client realTimeClient;

	/**
	 * @param configuration
	 * @param restClient
	 * @throws IOException
	 */
	public RealTimeClient(RealTimeSessionConfiguration configuration, SingularityRestClient restClient, String server)
			throws IOException {
		LOGGER.info("Starting real time client with configuration: [{}]", configuration);
		this.restClient = restClient;
		realTimeClient = new Client();
		realTimeClient.start();
		//KryoUtils.registerCommonClasses(realTimeClient.getKryo());
		KryoUtils.registerCommonClasses(realTimeClient.getKryo());
		realTimeClient.connect(8000, server, configuration.getPort());
        Log.set(Log.LEVEL_TRACE);
        LOGGER.info("Initiated");
    }

	public RealTimeClient(RealTimeSessionConfiguration configuration, SingularityRestClient restClient) throws IOException {
		this(configuration, restClient, ServerConstants.PRODUCTION_SERVER);
	}

	/**
	 *
	 */
	public void register() {
		Registration registrationAttempt = new Registration();
		Token token = restClient.renewIfNeededAndGetToken();
		registrationAttempt.setTokenId(token.getTokenId());
		registrationAttempt.setUserId(token.getUserId());
		registrationAttempt.setSession(getSessionType());
		LOGGER.info("Attempting to register TOKEN:" + token.getTokenId() + " UserId:" + token.getUserId() +
				" sessionType:" + getSessionType().toString());
		realTimeClient.sendTCP(registrationAttempt);
	}

	/**
	 * @return
	 */
	public abstract SessionType getSessionType();

}
