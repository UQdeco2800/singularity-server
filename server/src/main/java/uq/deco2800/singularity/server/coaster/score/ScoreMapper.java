package uq.deco2800.singularity.server.coaster.score;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.coaster.Score;


/**
 * A mapper to map the result returned by the database into a serialisable Score
 * class for the server to use.
 * 
 * @author Kellie Lutze
 *
 */
public class ScoreMapper implements ResultSetMapper<Score> {

	/* (non-Javadoc)
	* @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	*/
	@Override
	public Score map(int index, ResultSet result, StatementContext context) throws SQLException {
		return new Score(result.getString("HIGHSCOREID"))
				.setExperience(result.getString("EXPERIENCE"))
				.setTime(result.getString("TIME"))
				.setUserId(result.getString("USERID"))
				.setBossKills(result.getString("BOSSKILLS"))
				.setNetWorth(result.getString("NETWORTH"))
				.setPlayTime(result.getString("PLAYTIME"))
				.setKills(result.getString("KILLS"));
	}
}

