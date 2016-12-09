package uq.deco2800.singularity.server.pyramidscheme.achievements;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.pyramidscheme.Achievements;;

/**
 * DAO to retrieve, set, and update player statistics for pyramidScheme from the database
 * 
 * @author 1Jamster1
 *
 */
@RegisterMapper(AchievementMapper.class)
public interface AchievementDao {

	/**
	 * Finds all achievements for a particular user	
	 * @param userID
	 * @return
	 */
	@SqlQuery("select * from USERACHIEVES where USERID = :id order by TIMESTAMP DESC")
	List<Achievements> findAllStatistics(@Bind("id") String userID);
	
	/**
	 * Gets a list of all the User statistics currently in the database
	 * @return list of all User statistics, if none exist returns null
	 */
	@SqlQuery("select * from USERACHIEVES")
	List<Achievements> getAll();
	
	/**
	 * Gets a singular playerStat by the statID
	 * @param statID
	 * 			a String based UUID. Must not be null
	 * @return the User statistics if it exists, otherwise null
	 */
	@SqlQuery("select * from USERACHIEVES where STATID = :id")
	Achievements findByStatID(@Bind("id") String statID);
	
	/**
	 * Inserts a new record into the database under the USERSTATISTICS table
	 * @param userStatistics
	 * 			The statistics to add
	 * @return the number of rows added
	 */
	@SqlUpdate("insert into USERACHIEVES (STATID, USERID, NAME, TIMESTAMP) values " + "(:statID, :userID, :achievementName, :timestamp)")
	int insert(@BindBean Achievements Achievements);
}
