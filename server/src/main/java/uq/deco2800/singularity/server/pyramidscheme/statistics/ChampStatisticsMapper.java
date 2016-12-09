package uq.deco2800.singularity.server.pyramidscheme.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.pyramidscheme.ChampionStatistics;

/**
 * A mapper to map the result returned by the database into a serialisable UserStatistics
 * class for the server to use.
 * 
 * @author 1Jamster1
 *
 */
public class ChampStatisticsMapper implements ResultSetMapper<ChampionStatistics> {
	
	/* (non-Javadoc)
	 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	 */
	@Override
    public ChampionStatistics map(int index, ResultSet result, StatementContext context) throws SQLException {
        ChampionStatistics champStats = new ChampionStatistics(result.getString("STATID"));
        
		champStats.setUserID(result.getString("USERID"));
		champStats.setChampName(result.getString("CHAMPNAME"));
		champStats.setMinionsPlayed(result.getString("MINIONSPLAYED"));
        champStats.setMinionsKilled(result.getString("MINIONSKILLED"));
		champStats.setMinionsLost(result.getString("MINIONSLOST"));
		champStats.setHealthLost(result.getString("HEALTHLOST"));
		champStats.setHealthTaken(result.getString("HEALTHTAKEN"));
		champStats.setTotalWins(result.getString("TOTALWINS"));
		champStats.setTotalLosses(result.getString("TOTALLOSSES"));
		champStats.setTotalHours(result.getString("TOTALHOURS"));
		champStats.setTotalMinutes(result.getString("TOTALMINUTES"));
		champStats.setLastLogin(result.getString("TIMESTAMP"));
        
		return champStats;               
    }
}
