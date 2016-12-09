package uq.deco2800.singularity.server.authentication;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.Token;
import uq.deco2800.singularity.common.util.PasswordHashUtils;
import uq.deco2800.singularity.server.user.UserDao;

/**
 * A DataAccessObject used to access the {@link Token}s available in the
 * database. Provides the ability to insert, find or delete a token. Tokens
 * should be used to authenticate a user at all times and should be used in
 * conjunction with the {@link UserDao} and the {@link PasswordHashUtils} in
 * order to verify the credentials a potential user has given.
 * 
 * @author dion-loetscher
 *
 */
@RegisterMapper(TokenMapper.class)
public interface TokenDao {

	/**
	 * Retrieves exactly 1 token from the database from the given ID if the
	 * token exists
	 * 
	 * @param id
	 *            a String based UUID. Must not be null
	 * @return a token if one exists, else null.
	 */
	@SqlQuery("select * from TOKENS where TOKENID = :id")
	Token findByTokenId(@Bind("id") String id);

	/**
	 * Retrieves any number of tokens of a given user from the database when
	 * given a user's ID.
	 * 
	 * @param id
	 *            a String based UUID. Must not be null
	 * @return a List of Tokens containing all a user's tokens.
	 */
	@SqlQuery("select * from TOKENS where USERID = :id")
	List<Token> findByUserId(@Bind("id") String id);

	/**
	 * Deletes a token from the database from a given ID.
	 * 
	 * @param id
	 *            a String based UUID of a token. Must not be empty.
	 * @return The number of affected rows in the database.
	 */
	@SqlUpdate("delete from TOKENS where TOKENID = :id")
	int deleteByTokenId(@Bind("id") String id);

	/**
	 * Deletes all tokens of a given user from the database from a given ID.
	 * 
	 * @param id
	 *            a String based UUID of a user. Must not be empty.
	 * @return The number of affected rows in the database.
	 */
	@SqlUpdate("delete from TOKENS where USERID = :id")
	int deleteByUserId(@Bind("id") String id);

	/**
	 * Retrieves all the Tokens from the database
	 * 
	 * @return a List of Tokens. Will not be null.
	 */
	@SqlQuery("select * from TOKENS")
	List<Token> getAll();

	/**
	 * Inserts a new record into the user table in the database.
	 * 
	 * @param Token
	 *            The token object to insert.
	 * @return The number of inserted rows.
	 */
	@SqlUpdate("insert into TOKENS (TOKENID, EXPIRES, USERID) values " + "(:tokenId, :expires, :userId)")
	int insert(@BindBean Token token);

}
