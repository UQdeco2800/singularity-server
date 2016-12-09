package uq.deco2800.singularity.server.pyramidscheme.statistics;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.pyramidscheme.ChampionStatistics;

/**
 * DAO to retrieve, set, and update players champion statistics for 
 * pyramidScheme from the database
 * 
 * @author 1Jamster1
 *
 */
@RegisterMapper(ChampStatisticsMapper.class)
public interface ChampStatisticsDao {

	/**
     * Find current champion statistics for a specific champion
     * Retrieves 1 champion statistics from the database from the given the user id and champion name
     * This will be the most current statistics.
     * 
     * @param userID
     *            a String based UUID. Must not be null
     * @param name
     * 			  the Champions name. Must not be null
     * @return the Champion statistics if it exists, otherwise null.
     */
	@SqlQuery("select * from CHAMPIONSTATS where USERID = :id and CHAMPNAME = :name order"
			+ " by TIMESTAMP DESC FETCH FIRST 1 ROW ONLY")
	List<ChampionStatistics> findCurrentStatistics(@Bind("id") String userID, @Bind("name") String name);
	
	@SqlQuery("select * from CHAMPIONSTATS where USERID = :id and CHAMPNAME = :name "
			+ "order by TIMESTAMP DESC")
	List<ChampionStatistics> findAllStatistics(@Bind("id") String userID, @Bind("name") String name);
	
	/**
	 * Gets a list of all the champion statistics currently in the database
	 * @return list of all champion statistics, if none exist returns null
	 */
	@SqlQuery("select * from CHAMPIONSTATS")
	List<ChampionStatistics> getAll();
	
	/**
	 * Gets a singular champion statistics by the statID
	 * @param statID
	 * 			a String based UUID. Must not be null
	 * @return the champion statisitcs if it exists, otherwise null
	 */
	@SqlQuery("select * from CHAMPIONSTATS where STATID = :id")
	ChampionStatistics findByStatID(@Bind("id") String statID);
	
	/**
	 * Inserts a new record into the database under the CHAMPIONSTATS table
	 * @param champStatistics
	 * 			The statistics to add
	 * @return the number of rows added
	 */
	@SqlUpdate("insert into CHAMPIONSTATS (STATID, USERID, CHAMPNAME, MINIONSPLAYED, MINIONSKILLED," +
				"MINIONSLOST, HEALTHLOST, HEALTHTAKEN, TOTALWINS, TOTALLOSSES, TOTALHOURS, TOTALMINUTES, "
				+ "TIMESTAMP) values " + "(:statID, :userID, :champName, :minionsPlayed, :minionsKilled, "
				+ ":minionsLost, :healthLost, :healthTaken, :totalWins, :totalLosses, :totalHours, "
				+ ":totalMinutes, :lastLogin)")
	int insert(@BindBean ChampionStatistics champStatistics);
}
