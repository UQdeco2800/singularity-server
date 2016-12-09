package uq.deco2800.singularity.server.pyramidscheme.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.pyramidscheme.UserStatistics;

/**
 * A mapper to map the result returned by the database into a serialisable UserStatistics
 * class for the server to use.
 * 
 * @author 1Jamster1
 *
 */
public class StatisticsMapper implements ResultSetMapper<UserStatistics> {
	
	/* (non-Javadoc)
	 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	 */
	@Override
    public UserStatistics map(int index, ResultSet result, StatementContext context) throws SQLException {
		UserStatistics userStats =  new UserStatistics(result.getString("STATID"));
		
		userStats.setUserID(result.getString("USERID"));
		userStats.setUserLevel(result.getString("USERLEVEL"));
		userStats.setMinionsPlayed(result.getString("MINIONSPLAYED"));
		userStats.setMinionsKilled(result.getString("MINIONSKILLED"));
		userStats.setMinionsLost(result.getString("MINIONSLOST"));
		userStats.setHealthLost(result.getString("HEALTHLOST"));
		userStats.setHealthTaken(result.getString("HEALTHTAKEN"));
		userStats.setTotalWins(result.getString("TOTALWINS"));
		userStats.setTotalLosses(result.getString("TOTALLOSSES"));
		userStats.setTotalHours(result.getString("TOTALHOURS"));
		userStats.setTotalMinutes(result.getString("TOTALMINUTES"));
		userStats.setLastLogin(result.getString("TIMESTAMP"));
		
        return userStats;
	}           
}
