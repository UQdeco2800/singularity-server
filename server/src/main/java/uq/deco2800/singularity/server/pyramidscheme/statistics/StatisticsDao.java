package uq.deco2800.singularity.server.pyramidscheme.statistics;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.pyramidscheme.UserStatistics;

/**
 * DAO to retrieve, set, and update player statistics for pyramidScheme from the database
 * 
 * @author 1Jamster1
 *
 */
@RegisterMapper(StatisticsMapper.class)
public interface StatisticsDao {

	/**
     * Find current user statistics
     * Retrieves 1 User statistics from the database from the given the user id
     * This will be the most current statistics.
     * 
     * @param userID
     *            a String based UUID. Must not be null
     * @return the User statistics if it exists, otherwise null.
     */
	@SqlQuery("select * from USERSTATISTICS where USERID = :id order by TIMESTAMP DESC FETCH FIRST 1 ROW ONLY")
	List<UserStatistics> findCurrentStatistics(@Bind("id") String userID);
	
	@SqlQuery("select * from USERSTATISTICS where USERID = :id order by TIMESTAMP DESC")
	List<UserStatistics> findAllStatistics(@Bind("id") String userID);
	
	/**
	 * Gets a list of all the User statistics currently in the database
	 * @return list of all User statistics, if none exist returns null
	 */
	@SqlQuery("select * from USERSTATISTICS")
	List<UserStatistics> getAll();
	
	/**
	 * Gets a singular playerStat by the statID
	 * @param statID
	 * 			a String based UUID. Must not be null
	 * @return the User statistics if it exists, otherwise null
	 */
	@SqlQuery("select * from USERSTATISTICS where STATID = :id")
	UserStatistics findByStatID(@Bind("id") String statID);
	
	/**
	 * Inserts a new record into the database under the USERSTATISTICS table
	 * @param userStatistics
	 * 			The statistics to add
	 * @return the number of rows added
	 */
	@SqlUpdate("insert into USERSTATISTICS (STATID, USERID, USERLEVEL, MINIONSPLAYED, MINIONSKILLED," +
				"MINIONSLOST, HEALTHLOST, HEALTHTAKEN, TOTALWINS, TOTALLOSSES, TOTALHOURS, TOTALMINUTES, "
				+ "TIMESTAMP) values " + "(:statID, :userID, :userLevel, :minionsPlayed, :minionsKilled, "
				+ ":minionsLost, :healthLost, :healthTaken, :totalWins, :totalLosses, :totalHours, "
				+ ":totalMinutes, :lastLogin)")
	int insert(@BindBean UserStatistics userStatistics);
}
