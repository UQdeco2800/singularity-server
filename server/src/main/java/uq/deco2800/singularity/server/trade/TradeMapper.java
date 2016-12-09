package uq.deco2800.singularity.server.trade;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.Trade;



	
	/**
	 * A mapper to map the result returned by the database into a serialisable Trade
	 * class for the server to use.
	 * 
	 * @author Greg
	 *
	 */
	public class TradeMapper implements ResultSetMapper<Trade> {

		/* (non-Javadoc)
		 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
		 */
		@Override
		public Trade map(int index, ResultSet result, StatementContext context) throws SQLException {
			return new Trade(result.getString("COLLECTIONID"), result.getString("ITEMID")).setItemAmount(result.getString("ITEMAMOUNT"));
		}

	}


