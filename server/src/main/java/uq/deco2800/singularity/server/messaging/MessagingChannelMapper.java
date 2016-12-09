package uq.deco2800.singularity.server.messaging;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.MessageChannel;

public class MessagingChannelMapper implements ResultSetMapper<MessageChannel> {

	@Override
	public MessageChannel map(int index, ResultSet result, StatementContext context)
			throws SQLException {
		MessageChannel channel = new MessageChannel();
		channel.setChannelId(result.getString("CHANNELID"));
		channel.setUserId(result.getString("USERID"));
		return channel;
	}

}
