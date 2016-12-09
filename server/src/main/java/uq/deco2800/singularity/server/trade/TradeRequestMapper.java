package uq.deco2800.singularity.server.trade;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.TradeRequest;

/**
 * A mapper use to map the result returned by the database into a serialisable
 *  Trade Request class for the server to use.
 *  
 */


public class TradeRequestMapper implements ResultSetMapper<TradeRequest> {
	
	/* (non-Javadoc)
	 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	 */
	@Override
	public TradeRequest map(int index, ResultSet result, StatementContext context) throws SQLException {
		return new TradeRequest(
				result.getString("OFFEREDCOLLECTIONID"), 
				result.getString("USERID")).setRequestId(result.getString("REQUESTID"));
	}

}
