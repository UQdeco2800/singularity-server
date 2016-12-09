package uq.deco2800.singularity.server.trade;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.TradeResponse;
import uq.deco2800.singularity.server.trade.TradeResponseMapper;

/**
 * A DAO to retrieve, set and delete trade responses from the
 * database.
 * @author Gregory
 *
 */
@RegisterMapper(TradeResponseMapper.class)
public interface TradeResponseDao {
	/**
	 * Retrieves all the trade response from the database
	 * @return a list of trade response. Not null
	 */
	@SqlQuery("select * from TRADERESPONSE")
	List<TradeResponse> getAll();
	
	/**
	 * Retrieves exactly a trade response from the database from the given 
	 * User Id if there is a user responding to a trade 
	 * 
	 * @param id
	 *            a String based . Must not be null
	 * @return a trade response if one exists, else null.
	 */
	@SqlQuery("select * from TRADERESPONSE where USERID = :id")
	TradeResponse findById(@Bind("id") String id);

	
	
	/**
	 * Deletes a trade response from the database from a given user id.
	 * 
	 * 
	 * @param id
	 *            a String based UUID of a user id. Must not be empty.
	 * @return The number of affected rows in the database.
	 */
	@SqlUpdate("delete from TRADERESPONSE where USERID = :id")
	int deleteByRId(@Bind("id") String id);
	
	/**
	 * Inserts a new record into the Trade Response table in the database.
	 * 
	 * @param trade Response
	 *            The trade response object to insert.
	 * @return The number of inserted rows.
	 */
	@SqlUpdate("insert into TRADERESPONSE (USERID, ITEMID, COLLECTIONID) values "
			+ "(:userId,:itemId,:collectionId)")
	int insert(@BindBean TradeResponse tradeResponse);
	
	

}
