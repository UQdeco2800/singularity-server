package uq.deco2800.singularity.server.pyramidscheme.achievements;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.pyramidscheme.Achievements;

/**
 * A mapper to map the result returned by the database into a serialisable UserStatistics
 * class for the server to use.
 * 
 * @author 1Jamster1
 *
 */
public class AchievementMapper implements ResultSetMapper<Achievements> {
	
	/* (non-Javadoc)
	 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	 */
	@Override
    public Achievements map(int index, ResultSet result, StatementContext context) throws SQLException {
		Achievements achievement =  new Achievements(result.getString("STATID"));
		
		achievement.setUserID(result.getString("USERID"));
		achievement.setAchievementName(result.getString("NAME"));
		achievement.setTimestamp(result.getString("TIMESTAMP"));
		
        return achievement;
	}           
}
