package com.recipes.FlavourFoundry.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

@Repository
public class LevelRepo {

    @Autowired
    JdbcTemplate template;

    public static String SQL_GET_LEVELSTATS = "select * from level_tiers where level = ?";

    public Optional<SqlRowSet> getLevelStats(Integer level) {
        SqlRowSet rs = template.queryForRowSet(SQL_GET_LEVELSTATS, level);
        if (rs.next()) {
            return Optional.of(rs);
        }
        return Optional.empty();
    }

}
