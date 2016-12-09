package uq.deco2800.singularity.server.achievement;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import uq.deco2800.singularity.common.representations.duxcom.Achievement;
import uq.deco2800.singularity.server.duxcom.savegame.SaveGameMapper;

import java.util.List;

/**
 * A Data Access Object (DAO) to retrieve, set, update and delete Achievements from the
 * database.
 *
 * @author Daniel Gormly (Slack: Gormly)
 *
 */
@RegisterMapper(AchievementMapper.class)
public interface AchievementDao {

    /**
     * Retrieves all the achievements from the database
     *
     * @return a List of achievements. Will not be null.
     */
    @SqlQuery("select * from ACHIEVEMENTS")
    List<Achievement> getAll();

    /**
     * Retrieves exactly 1 achievement from the database from the given ID if the achievement
     * exists
     *
     * @param id
     *            a String based UUID. Must not be null
     * @return an achievement if one exists, else null.
     */
    @SqlQuery("select * from ACHIEVEMENTS where ID = :id")
    Achievement findById(@Bind("id") String id);

    /**
     * Retrieves exactly 1 achievement from the database from the given achievement name.
     *
     * @param name
     *            The of the achievement. Must not be null.
     * @return
     */
    @SqlQuery("select * from ACHIEVEMENTS where NAME = :name")
    Achievement findByName(@Bind("name") String name);

    /**
     * Deletes an achievement from the database with a given ID.
     *
     * @param id
     *            a String based UUID of an achievement. Must not be empty.
     * @return The number of affected rows in the database.
     */
    @SqlUpdate("delete from ACHIEVEMENTS where ID = :id")
    int deleteById(@Bind("id") String id);

    /**
     * Updates an achievement's name and description.
     *
     * @param achievement
     *            The updated user object.
     * @return The number of affected records.
     */
    @SqlUpdate("update into ACHIEVEMENTS set (NAME, DESCRIPTION) = (:name, :description) where ID = :Id")
    int update(@BindBean Achievement achievement);

    /**
     * Inserts a new record into the achievement table in the database.
     *
     * @param achievement
     *            The Achievement object to insert.
     * @return The number of inserted rows.
     */
    @SqlUpdate("insert into ACHIEVEMENTS (ID, NAME, DESCRIPTION, TYPE, SCORE) values "
            + "(:id, :name, :description, :type, :score)")
    int insert(@BindBean Achievement achievement);
}
