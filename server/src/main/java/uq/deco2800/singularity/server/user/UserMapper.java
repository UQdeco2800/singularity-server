package uq.deco2800.singularity.server.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.User;

/**
 * A mapper to map the result returned by the database into a serialisable User
 * class for the server to use.
 * 
 * @author dloetscher
 *
 */
public class UserMapper implements ResultSetMapper<User> {

	/* (non-Javadoc)
	 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	 */
	@Override
	public User map(int index, ResultSet result, StatementContext context) throws SQLException {
		return new User(result.getString("USERID"), result.getString("USERNAME")).setFirstName(result.getString("FIRSTNAME"))
				.setLastName(result.getString("LASTNAME")).setMiddleName(result.getString("MIDDLENAME"))
				.setPassword(result.getString("PASSWORD")).setSalt(result.getString("SALT"));
	}

}
