package uq.deco2800.singularity.server.pyramidscheme.state;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.pyramidscheme.GameState;

/**
 * Save and load a pyramid scheme game data from the database
 * 
 * @author tris10au
 *
 */
@RegisterMapper(StateMapper.class)
public interface StateDao {

	/**
     * Find the latest save state for a user
     * Retrieves 1 Game State from the database for the given the user id
     * This will be the latest save state.
     * 
     * @param userID
     *            a String based UUID. Must not be null
     * @return a single-element list of Game State if it exists, otherwise null.
     */
	@SqlQuery("select * from GAMESTATE where USERID = :id order by SAVETIME DESC FETCH FIRST 1 ROW ONLY")
	List<GameState> findCurrentGameState(@Bind("id") String userID);
	
	/**
	 * Gets a list of all the Game states currently in the database
	 * @return list of all Game States, if none exist returns null
	 */
	@SqlQuery("select * from GAMESTATE")
	List<GameState> getAll();
	
	/**
	 * Gets a singular Game state by the stateID
	 * @param stateID
	 * 			a String based UUID. Must not be null
	 * @return the Game State if it exists, otherwise null
	 */
	@SqlQuery("select * from GAMESTATE where STATEID = :id")
	GameState findByStateID(@Bind("id") String stateID);
	
	/**
	 * Inserts a new record into the database under the GAMESTATE table
	 * @param GameState
	 * 			The GameState to add
	 * @return the number of rows added
	 */
	@SqlUpdate("insert into GAMESTATE (STATEID, USERID, SAVETIME, GAMEDATA) values "
				+ "(:stateID, :userID, :saveTime, :data)")
	int insert(@BindBean GameState gameState);
}
