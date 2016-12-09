package uq.deco2800.singularity.server.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uq.deco2800.singularity.common.representations.MessageChannel;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;
import uq.deco2800.singularity.server.authentication.TokenDao;
import uq.deco2800.singularity.server.realtime.RealTimeService;

/**
 * The Messaging Service used to handle all the messages which get sent to this server and forward to other users.
 * Extends from the {@link RealTimeService} which provides the initial set up in {@link #start()}.
 * 
 * @author dion-loetscher
 *
 */
public class MessagingService extends RealTimeService {

	/**
	 * A String representation of this class. Primarily used for Logging.
	 */
	private static final String CLASS = MessagingService.class.getName();

	/**
	 * {@link Logger} from SL4J used to log at different levels.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	/**
	 * The DataAccessObject for all {@link MessageChannel MessageChannels}. Used to find, insert and delete channels
	 * between users for messaging.
	 */
	private final MessagingChannelDao messageChannelDao;

	/**
	 * The DataAccessObject for all tokens. Used to find, insert and delete tokens.
	 */
	private final TokenDao tokenDao;

	/**
	 * Constructor for the MessagingService. Used to pass in the configuration and DAOs required to handle messaging
	 * Calls {@link RealTimeService#RealTimeService(RealTimeSessionConfiguration)}
	 * 
	 * @param configuration A {@link RealTimeSessionConfiguration} used to define the settings for this real time service. Should not be null and should 
	 * @param messageChannelDao The DAO for MessageChannels
	 * @param tokenDao
	 */
	public MessagingService(RealTimeSessionConfiguration configuration, MessagingChannelDao messageChannelDao,
			TokenDao tokenDao) {
		super(configuration);
		this.messageChannelDao = messageChannelDao;
		this.tokenDao = tokenDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uq.deco2800.singularity.server.realtime.RealTimeService#start()
	 */
	@Override
	public boolean start() {
		boolean success = super.start();
		server.addListener(new MessagingListener(messageChannelDao, tokenDao));
		return success;
	}
	
}
