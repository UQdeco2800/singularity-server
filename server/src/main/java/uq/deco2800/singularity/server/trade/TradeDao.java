package uq.deco2800.singularity.server.trade;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.Trade;
import uq.deco2800.singularity.server.trade.TradeMapper;

/**
 * A Data Access Object (DAO) to retrieve, set, update and delete users from the
 * database.
 * 
 * @author Greg
 *
 */

@RegisterMapper(TradeMapper.class)
public interface TradeDao {
	/**
	 * Retrieves all the Collection from the database
	 * 
	 * @return a List of Collection. Will not be null.
	 */
	@SqlQuery("select * from COLLECTION")
	List<Trade> getAll();
	/**
	 * Retrieves the collection ID for a given collectionID that
	 * exists
	 * 
	 * @param id
	 *            a String based UUID. Must not be null
	 * @return a Collection if one exists, else null.
	 */
	@SqlQuery("select * from COLLECTION where COLLECTIONID = :id")
	Trade findById(@Bind("id") String id);
	/**
	 * Inserts a new record into the collection table database
	 * 
	 * @param collection 
	 * 		 The collection object to insert.
	 * @return The number of inserted rows.
	 */
	@SqlUpdate("insert into COLLECTION (COLLECTIONID, ITEMID, ITEMAMOUNT) " +
	 "values (:collectionId, :itemId, :itemAmount)")
	int insert(@BindBean Trade trade);
	/**
	 * Retrieves exactly 1 collection from the database from the given trade
	 * 
	 * @param Item Id
	 *            The itemID of the item in collection. Must not be null.
	 * @return
	 */
	@SqlQuery("select * from COLLECTION where ITEMID = :itemId")
	Trade findByItemID(@Bind("itemId") String itemId);
	

}