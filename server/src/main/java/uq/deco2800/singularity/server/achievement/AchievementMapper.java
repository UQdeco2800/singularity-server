package uq.deco2800.singularity.server.achievement;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import uq.deco2800.singularity.common.representations.duxcom.Achievement;
import uq.deco2800.singularity.common.representations.duxcom.AchievementType;
import uq.deco2800.singularity.common.representations.duxcom.PlayerStats;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Gormly on 14/09/2016.
 */
public class AchievementMapper implements ResultSetMapper<Achievement>{

    @Override
    public Achievement map(int index, ResultSet result, StatementContext context) throws SQLException {
        Achievement achievement = new Achievement();
        achievement.setId(result.getString("ID"));
        achievement.setName(result.getString("NAME"));
        achievement.setDescription(result.getString("DESCRIPTION"));
        String type = result.getString("TYPE");
        achievement.setScore(result.getInt("SCORE"));
        switch (type) {
        case "TIME":
            achievement.setType(AchievementType.TIME);
            
        case "KILL":
            achievement.setType(AchievementType.KILL);
        }
        return achievement;
    }
}

