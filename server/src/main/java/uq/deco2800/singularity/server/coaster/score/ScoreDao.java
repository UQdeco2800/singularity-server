package uq.deco2800.singularity.server.coaster.score;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.coaster.Score;

@RegisterMapper(ScoreMapper.class)
public interface ScoreDao {

	/**
	 * Retrieves exactly 1 Score from the database from the given ID of the score
	 * exists
	 * 
	 * @param id
	 *            a String based UUID. Must not be null
	 * @return a score if one exists, else null.
	 */
	@SqlQuery("select * from HIGHSCORE where HIGHSCOREID = :id")
	Score findById(@Bind("id") String id);
	
	@SqlQuery("select * from HIGHSCORE where USERID = :id order by EXPERIENCE DESC FETCH FIRST 10 ROWS ONLY")
	List<Score> findHighestScoresByExperienceAndId(@Bind("id") String id);
	
	@SqlQuery("select * from HIGHSCORE order by EXPERIENCE DESC FETCH FIRST 10 ROWS ONLY")
	List<Score> findHighestScoresByTypeExperience();
	
	@SqlQuery("select * from HIGHSCORE where USERID = :id order by KILLS DESC FETCH FIRST 10 ROWS ONLY")
	List<Score> findHighestScoresByKillsAndId(@Bind("id") String id);
	
	@SqlQuery("select * from HIGHSCORE order by KILLS DESC FETCH FIRST 10 ROWS ONLY")
	List<Score> findHighestScoresByTypeKills();
	
	@SqlQuery("select * from HIGHSCORE where USERID = :id order by BOSSKILLS DESC FETCH FIRST 10 ROWS ONLY")
	List<Score> findHighestScoresByBossKillsAndId(@Bind("id") String id);
	
	@SqlQuery("select * from HIGHSCORE order by BOSSKILLS DESC FETCH FIRST 10 ROWS ONLY")
	List<Score> findHighestScoresByTypeBossKills();
	
	@SqlQuery("select * from HIGHSCORE where USERID = :id order by PLAYTIME ASC FETCH FIRST 10 ROWS ONLY")
	List<Score> findHighestScoresByPlayTimeAndId(@Bind("id") String id);
	
	@SqlQuery("select * from HIGHSCORE order by PLAYTIME ASC FETCH FIRST 10 ROWS ONLY")
	List<Score> findHighestScoresByTypePlayTime();
	
	@SqlQuery("select * from HIGHSCORE where USERID = :id order by NETWORTH DESC FETCH FIRST 10 ROWS ONLY")
	List<Score> findHighestScoresByNetWorthAndId(@Bind("id") String id);
	
	@SqlQuery("select * from HIGHSCORE order by NETWORTH DESC FETCH FIRST 10 ROWS ONLY")
	List<Score> findHighestScoresByTypeNetWorth();
	
	/**
	 * Inserts a new record into the HIGHSCORE table in the database.
	 * 
	 * @param Score
	 *            The Score object to insert.
	 * @return The number of inserted rows.
	 */
	@SqlUpdate("insert into HIGHSCORE (HIGHSCOREID, EXPERIENCE, TIME, PLAYTIME, KILLS, BOSSKILLS, NETWORTH, USERID) values "
			+ "(:scoreId, :experience, :time, :playTime, :kills, :bossKills, :netWorth, :userId)")
	int insert(@BindBean Score score);
}
