package uq.deco2800.singularity.server.pyramidscheme.state;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.pyramidscheme.GameState;

/**
 * A mapper to map the result returned by the database into a serialisable GameState
 * class for the server to use.
 * 
 * @author tris10au
 *
 */
public class StateMapper implements ResultSetMapper<GameState> {
	
	/* (non-Javadoc)
	 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	 */
	@Override
    public GameState map(int index, ResultSet result, StatementContext context) throws SQLException {
        return ((GameState) new GameState(result.getString("STATEID")))
                .setUserID(result.getString("USERID"))
                .setSaveTime(result.getString("TIMESTAMP"))
                .setData(result.getString("GAMEDATA"));
    }
}
