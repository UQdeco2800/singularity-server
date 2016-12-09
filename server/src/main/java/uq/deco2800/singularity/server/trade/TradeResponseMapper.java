package uq.deco2800.singularity.server.trade;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.TradeResponse;

/**
 * A mapper use to map the result returned by the database into a serialisable
 *  Trade Response class for the server to use.
 *  
 */


public class TradeResponseMapper implements ResultSetMapper<TradeResponse> {
	
	@Override
	public TradeResponse map(int index, ResultSet result, StatementContext context) throws SQLException {
		return new TradeResponse(result.getString("USERID"), result.getString("ITEMID"), result.getString("COLLECTIONID"));
	}

}
