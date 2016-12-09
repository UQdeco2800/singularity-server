package uq.deco2800.singularity.server.duxcom.savegame;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.duxcom.PlayerStats;

/**
 * DAO to retrieve, set, and update player stats for duxcom from the database
 * 
 * @author jhess-osum
 *
 */
@RegisterMapper(SaveGameMapper.class)
public interface SaveGameDao {
    
    /**
     * Find current score
     * Retrieves 1 score from the database from the given user id
     * This will be the most current score.
     * 
     * @param user id
     *            a String based UUID. Must not be null
     * @return a playerStat if one exists, else null.
     */
    @SqlQuery("select * from PLAYERSTATS where USERID = :id order by TIMESTAMP DESC FETCH FIRST 1 ROW ONLY")
    List<PlayerStats> findCurrentScore(@Bind("id") String id);
    
    /**
     * Find User' high scores (top 5)
     * Retrieves 5 scores from the data base from the given user id
     * 
     * @param user id
     *            a String based UUID. must not be null
     * @return plyerStats if one exists, else null
     */
    @SqlQuery("select * from PLAYERSTATS where USERID = :id order by SCORE DESC FETCH FIRST 5 ROWS ONLY")
    List<PlayerStats> findUserHighScore(@Bind("id") String id);
    
    /**
     * Find high scores (top 10) by type: Kills
     * Retrieves 10 scores from the data base from the given user id
     * 
     * @param user id
     *            a String based UUID. must not be null
     * @return plyerStats if one exists, else null
     */
    @SqlQuery("select * from PLAYERSTATS order by SCORE DESC FETCH FIRST 10 ROWS ONLY")
    List<PlayerStats> findScoreHighScores();
    
    /**
     * Find high scores (top 10) by type: Score
     * Retrieves 10 scores from the data base from the given user id
     * 
     * @param user id
     *            a String based UUID. must not be null
     * @return plyerStats if one exists, else null
     */
    @SqlQuery("select * from PLAYERSTATS order by KILLS DESC FETCH FIRST 10 ROWS ONLY")
    List<PlayerStats> findKillsHighScores();
    
    
    /**
     * return a list of all entries in PLAYERSTATS
     * 
     * @return a score if one exists, else null.
     */
    @SqlQuery("select * from PLAYERSTATS")
    List<PlayerStats> getAll();
    
    /**
     * Finds 1 entry in db table given the scoreId
     * 
     * @param scoreId
     *            The scoreId of the entry
     * @return a playStat if one exists, else null
     */
    @SqlQuery("select * from PLAYERSTATS where SCOREID = :id")
    PlayerStats findByScoreId(@Bind("id") String id);
    
    
    /**
     * Inserts a new record into the PLAYERSTATS table in the database.
     * 
     * @param SaveGame
     *            The SaveGame object to insert.
     * @return The number of inserted rows.
     */
    @SqlUpdate("insert into PLAYERSTATS (SCOREID, USERID, TIMESTAMP, KILLS, SCORE) values "
            + "(:scoreId, :userId, :timestamp, :kills, :score)")
    int insert(@BindBean PlayerStats playerStats);
    
    

    
    
}
