package uq.deco2800.singularity.server.duxcom.savegame;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import uq.deco2800.singularity.common.representations.duxcom.PlayerStats;

/**
 * A mapper to map the result returned by the database into a serialisable SaveGame
 * class for the server to use.
 * 
 * @author jhess-osum
 *
 */
public class SaveGameMapper implements ResultSetMapper<PlayerStats>{
    
    /* (non-Javadoc)
    * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
    */
    @Override
    public PlayerStats map(int index, ResultSet result, StatementContext context) throws SQLException {
        PlayerStats playerStats = new PlayerStats();
        playerStats.setScoreId(result.getString("SCOREID"));
        playerStats.setUserId(result.getString("USERID"));
        playerStats.setTimestamp(result.getString("TIMESTAMP"));
        playerStats.setKills(result.getString("KILLS"));
        playerStats.setScore(result.getString("SCORE"));
        return playerStats;
    }

}
