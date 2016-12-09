package uq.deco2800.singularity.server;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;
import uq.deco2800.singularity.server.authentication.TokenDao;
import uq.deco2800.singularity.server.authentication.TokenResource;
import uq.deco2800.singularity.server.coaster.score.ScoreDao;
import uq.deco2800.singularity.server.coaster.score.ScoreResource;
import uq.deco2800.singularity.server.duxcom.savegame.SaveGameDao;
import uq.deco2800.singularity.server.duxcom.savegame.SaveGameResource;
import uq.deco2800.singularity.server.messaging.MessagingChannelDao;
import uq.deco2800.singularity.server.messaging.MessagingChannelResource;
import uq.deco2800.singularity.server.messaging.MessagingService;
import uq.deco2800.singularity.server.messaging.MessagingServiceHealthCheck;
import uq.deco2800.singularity.server.pyramidscheme.achievements.AchievementDao;
import uq.deco2800.singularity.server.pyramidscheme.achievements.AchievementResource;
import uq.deco2800.singularity.server.pyramidscheme.statistics.ChampStatisticsDao;
import uq.deco2800.singularity.server.pyramidscheme.statistics.ChampStatisticsResource;
import uq.deco2800.singularity.server.pyramidscheme.statistics.StatisticsDao;
import uq.deco2800.singularity.server.pyramidscheme.statistics.StatisticsResource;
import uq.deco2800.singularity.server.realtime.LoggingService;
import uq.deco2800.singularity.server.realtime.RealTimeResource;
import uq.deco2800.singularity.server.realtime.RealTimeService;
import uq.deco2800.singularity.server.user.UserDao;
import uq.deco2800.singularity.server.user.UserResource;
import uq.deco2800.singularity.server.trade.TradeDao;
import uq.deco2800.singularity.server.trade.TradeRequestDao;
import uq.deco2800.singularity.server.trade.TradeRequestResource;
import uq.deco2800.singularity.server.trade.TradeResource;
import uq.deco2800.singularity.server.trade.TradeResponseDao;
import uq.deco2800.singularity.server.trade.TradeResponseResource;

/**
 * The core of the server. Contains the main function which is run.
 * 
 * @author dloetscher
 *
 */
public class SingularityServer extends Application<ServerConfiguration> {

	private List<RealTimeService> liveServices = new LinkedList<>();
	private RealTimeResource realTimeResource;
	private Server jettyServer;

	private static final String CLASS = SingularityServer.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

	/**
	 * Starts the server.
	 * 
	 * @param args
	 *            The arguments that are passed to the server. Refer to {@link #run(String...)}
	 * @throws Exception
	 *             Something went wrong.
	 */
	public static void main(String[] args) throws Exception {
		SingularityServer server = new SingularityServer();
		server.run(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.dropwizard.Application#initialize(io.dropwizard.setup.Bootstrap)
	 */
	@Override
	public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
		bootstrap.addBundle(new DBIExceptionsBundle());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.dropwizard.Application#run(io.dropwizard.Configuration, io.dropwizard.setup.Environment)
	 */
	@Override
	public void run(ServerConfiguration configuration, Environment environment) {
		// Setup JDBI
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "derby");

		// Data Access Object Setup
		final UserDao userDao = jdbi.onDemand(UserDao.class);
		final TradeRequestDao tradeRequestDao= jdbi.onDemand(TradeRequestDao.class);

		final TradeDao tradeDao = jdbi.onDemand(TradeDao.class);
		final TokenDao tokenDao = jdbi.onDemand(TokenDao.class);
		final MessagingChannelDao messageChannelDao = jdbi.onDemand(MessagingChannelDao.class);
		final ScoreDao scoreDao = jdbi.onDemand(ScoreDao.class);


		final TradeResponseDao tradeResponseDao = jdbi.onDemand(TradeResponseDao.class);

		final SaveGameDao saveGameDao = jdbi.onDemand(SaveGameDao.class);
		final StatisticsDao statisticsDao = jdbi.onDemand(StatisticsDao.class);
		final ChampStatisticsDao champStatisticsDao = jdbi.onDemand(ChampStatisticsDao.class);
		final AchievementDao achieveDao = jdbi.onDemand(AchievementDao.class);

		// Resource Setup
		environment.jersey().register(new TradeResource(tradeDao));
		environment.jersey().register(new TradeRequestResource(tradeRequestDao));
		environment.jersey().register(new UserResource(userDao));
		environment.jersey().register(new TradeResponseResource(tradeResponseDao,userDao,tokenDao));
		environment.jersey().register(new TokenResource(tokenDao, userDao));
		environment.jersey().register(new MessagingChannelResource(messageChannelDao, tokenDao));
		environment.jersey().register(new ScoreResource(scoreDao, userDao, tokenDao));
		environment.jersey().register(new SaveGameResource(saveGameDao, userDao, tokenDao));
		environment.jersey().register(new StatisticsResource(statisticsDao, userDao, tokenDao));
		environment.jersey().register(new ChampStatisticsResource(champStatisticsDao, userDao, tokenDao));
		environment.jersey().register(new AchievementResource(achieveDao, userDao, tokenDao));
		
		// Keep reference to real time resource so that real time sessions can be shut down.
		realTimeResource = new RealTimeResource(tokenDao);
		environment.jersey().register(realTimeResource);

		// Add services which run for entire server lifetime and store to later shut down
		
		if (configuration.loggingServiceShouldStart()) {
    		RealTimeSessionConfiguration loggingConfiguration = new RealTimeSessionConfiguration();
    		loggingConfiguration.setPort(8090);
    		loggingConfiguration.setSession(SessionType.LOGGING);
    		LoggingService loggingService = new LoggingService(loggingConfiguration);
    		loggingService.start();
    		liveServices.add(loggingService);
		}
		
		MessagingService messageServer = new MessagingService(configuration.getMessagingConfiguration(),
				messageChannelDao, tokenDao);
		environment.healthChecks().register("messageServer", new MessagingServiceHealthCheck(messageServer));
		liveServices.add(messageServer);
		
		// Start services once restful API started
		environment.lifecycle().addServerLifecycleListener(new ServerLifecycleListener() {

			@Override
			public void serverStarted(Server server) {
				// save jetty server for later shut down
				jettyServer = server;

				// start all real time services
				messageServer.start();
			}

		});
	}

	/**
	 * Shuts down all real time services as well as the Jetty server running the Singularity REST API.
	 */
	public void stopAllServices() {
		LOGGER.info("Server is shutting down");
		for (RealTimeService service : liveServices) {
			service.stop();
		}
		realTimeResource.shutdownLiveServices();
		try {
			jettyServer.stop();
		} catch (Exception exception) {
			LOGGER.error("Exception occurred when shutting down", exception);
			System.exit(1); // Force exit if server shut down hangs

		}
	}

}
