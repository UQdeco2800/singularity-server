package uq.deco2800.singularity.server.authentication;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.Token;

/**
 * A mapper to map the result returned by the database into a serialisable Token
 * class for the server to use.
 * 
 * @author dion-loetscher
 *
 */
public class TokenMapper implements ResultSetMapper<Token> {

	/* (non-Javadoc)
	 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	 */
	@Override
	public Token map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new Token(r.getString("TOKENID"), r.getLong("EXPIRES"), r.getString("USERID"));
	}

}
