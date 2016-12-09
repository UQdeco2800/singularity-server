package uq.deco2800.singularity.server.messaging;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.MessageChannel;

@RegisterMapper(MessagingChannelMapper.class)
public interface MessagingChannelDao {
	
	@SqlQuery("select * from MESSAGECHANNELS where CHANNELID = :id")
	List<MessageChannel> getMessageChannelsByChannelId(@Bind("id") String id);
	
	@SqlQuery("select * from MESSAGECHANNELS where USERID = :id")
	List<MessageChannel> getMessageChannelsByUserId(@Bind("id") String id);
	
	@SqlQuery("select * from MESSAGECHANNELS where USERID =:userId AND CHANNELID = :channelId ")
	MessageChannel getUsersChannelRecord(@Bind("channelId") String channelId, @Bind("userId") String userId);
	
	/**
	 * Inserts a new record into the user table in the database.
	 * 
	 * @param user
	 *            The user object to insert.
	 * @return The number of inserted rows.
	 */
	@SqlUpdate("insert into MESSAGECHANNELS (CHANNELID, USERID) values "
			+ "(:channelId, :userId)")
	int insert(@BindBean MessageChannel channel);
	/**
	 * Remove a record from the user table in the database.
	 * 
	 * @param user
	 *            The user object to remove.
	 * @return The number of removed rows.
	 */
	@SqlUpdate("DELETE FROM MESSAGECHANNELS (CHANNELID, USERID) values "
			+ "(:channelId, :userId)")
	int remove(@BindBean MessageChannel channel);


}
