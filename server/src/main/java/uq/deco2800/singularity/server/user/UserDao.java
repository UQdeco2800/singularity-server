package uq.deco2800.singularity.server.user;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.User;

/**
 * A Data Access Object (DAO) to retrieve, set, update and delete users from the
 * database.
 * 
 * @author dloetscher
 *
 */
@RegisterMapper(UserMapper.class)
public interface UserDao {

	/**
	 * Retrieves all the users from the database
	 * 
	 * @return a List of Users. Will not be null.
	 */
	@SqlQuery("select * from USERS")
	List<User> getAll();

	/**
	 * Retrieves exactly 1 user from the database from the given ID if the user
	 * exists
	 * 
	 * @param id
	 *            a String based UUID. Must not be null
	 * @return a user if one exists, else null.
	 */
	@SqlQuery("select * from USERS where USERID = :id")
	User findById(@Bind("id") String id);

	/**
	 * Retrieves exactly 1 user from the database from the given username
	 * 
	 * @param username
	 *            The username of the user. Must not be null.
	 * @return
	 */
	@SqlQuery("select * from USERS where USERNAME = :username")
	User findByUsername(@Bind("username") String username);

	/**
	 * Deletes a user from the database from a given ID.
	 * 
	 * @param id
	 *            a String based UUID of a user. Must not be empty.
	 * @return The number of affected rows in the database.
	 */
	@SqlUpdate("delete from USERS where USERID = :id")
	int deleteById(@Bind("id") String id);

	/**
	 * Updates a user's first, middle and last name.
	 * 
	 * @param user
	 *            The updated user object.
	 * @return The number of affected records.
	 */
	@SqlUpdate("update into USERS set (FIRSTNAME, LASTNAME, MIDDLENAME) = (:firstName, :lastName, :middleName) where ID = :userId")
	int update(@BindBean User user);

	/**
	 * Inserts a new record into the user table in the database.
	 * 
	 * @param user
	 *            The user object to insert.
	 * @return The number of inserted rows.
	 */
	@SqlUpdate("insert into USERS (USERID, USERNAME, FIRSTNAME, LASTNAME, MIDDLENAME, PASSWORD, SALT) values "
			+ "(:userId, :username, :firstName, :lastName, :middleName, :password, :salt)")
	int insert(@BindBean User user);
}
