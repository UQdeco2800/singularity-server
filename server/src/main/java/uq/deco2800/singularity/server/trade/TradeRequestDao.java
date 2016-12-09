package uq.deco2800.singularity.server.trade;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.TradeRequest;
import uq.deco2800.singularity.server.trade.TradeRequestMapper;

/**
 * A DAO to retrieve, set and delete trade request from the
 * database.
 * @author Gregory
 *
 */
@RegisterMapper(TradeRequestMapper.class)
public interface TradeRequestDao {
	/**
	 * Retrieves all the trade request from the database
	 * @return a list of trade request. Not null
	 */
	@SqlQuery("select * from TRADEREQUESTS")
	List<TradeRequest> getAll();
	
	/**
	 * Retrieves exactly a trade request from the database from the given 
	 * request Id if the trade request exists
	 * 
	 * @param id
	 *            a String based . Must not be null
	 * @return a trade request if one exists, else null.
	 */
	@SqlQuery("select * from TRADEREQUESTS where REQUESTID = :Rid")
	TradeRequest findByRequestId(@Bind("Rid") String Rid);

	/**
	 * Retrieves exactly a trade request from the database from the given
	 * offered collection Id
	 * @param offered Collection Id
	 *            The offered Collection Id of a trade request. Must not be null.
	 * @return
	 */
	@SqlQuery("select * from TRADEREQUESTS where OFFEREDCOLLECTIONID = :Cid")
	TradeRequest findByCId(@Bind("Cid") String Cid);
	
	/**
	 * Retrieves exactly a trade request from the database from the given
	 * offered collection Id
	 * @param Cid
	 * @return
	 */
	@SqlQuery("select * from TRADEREQUESTS where USERID = :Uid")
	List<TradeRequest> findByUId(@Bind("Uid") String Uid);
	
	
	
	@SqlQuery("select * from TRADEREQUESTS where USERID != :Uid")
	List<TradeRequest> findAndExcludeUId(@Bind("Uid") String Uid);
	
	/**
	 * Deletes a trade request from the database from a given request id.
	 * 
	 * 
	 * @param id
	 *            a String based UUID of a trade request. Must not be empty.
	 * @return The number of affected rows in the database.
	 */
	@SqlUpdate("delete from TRADEREQUESTS where REQUESTID = :Rid")
	int deleteByRId(@Bind("Rid") String Rid);
	
	/**
	 * Inserts a new record into the Trade Request table in the database.
	 * 
	 * @param trade Request
	 *            The trade request object to insert.
	 * @return The number of inserted rows.
	 */
	@SqlUpdate("insert into TRADEREQUESTS (REQUESTID, OFFEREDCOLLECTIONID, USERID) values "
			+ "(:requestId, :offeredCollectionId, :userId)")
	int insert(@BindBean TradeRequest tradeRequest);

}
