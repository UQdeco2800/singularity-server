package uq.deco2800.singularity.common;

public class ServerConstants {

	/* HTTP/S Protocol */
	public static final String HTTP_PROTOCOL = "http://";

	/* Server Default Ports */
	public static final int REST_PORT = 8080;
	public static final int REST_ADMIN_PORT = 8081;
	public static final int MESSAGING_PORT = 8888;

	/* Server Default hosts */
	public static final String LOCAL_HOST = "localhost";
	public static final String PRODUCTION_SERVER = "singularity.rubberducky.io";

	/* Resource Constants */
	public static final String AUTHENTICATION_RESOURCE = "/token";
	public static final String MESSAGE_CHANNEL_RESOURCE = "/messaging";
	public static final String USER_RESOURCE = "/user";
	public static final String TRADE_RESOURCE = "/trade";
	public static final String OTHER_RESOURCE = "/other";
	public static final String TRADEREQUEST_RESOURCE = "/tradeRequest";
	public static final String TRADERESPONSE_RESOURCE = "/tradeResponse";
	public static final String MESSAGE_CHANNEL_PARTICIPANTS = "/participants";
	public static final String REAL_TIME_RESOURCE = "/games";
	public static final String SCORE_RESOURCE = "/score";
	public static final String PLAYER_STATS_RESOURCE = "/savegame";
	public static final String STATISTICS_RESOURCE = "/statistics";
	/* Action Constants */
	public static final String NEW = "/new";
	public static final String RENEW = "/renew";
	public static final String REMOVE = "/remove";

	public class DuxcomConstants {
		public static final String GAME_STATUS_RESOURCE = "/duxcom_gamestatus";
	}
}
