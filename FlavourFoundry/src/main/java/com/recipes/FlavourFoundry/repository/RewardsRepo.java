package com.recipes.FlavourFoundry.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.recipes.FlavourFoundry.model.User;

@Repository
public class RewardsRepo {

    @Autowired
    JdbcTemplate template;

    public static String SQL_GET_REWARD_EXP = "select exp from rewards where tier = ? and act = ?";
    public static String SQL_GET_GATEKEEP_REWARD = "select exp, credit from rewards where tier = ? and act = ? and isGatekeep = true";
    public static String SQL_GET_NONGATEKEEP_DATA = "select tier, act, exp from rewards where isGatekeep = false";
    public static String SQL_GET_GATEKEEP_DATA = "select tier, act, exp, credit from rewards where isGatekeep = true";

    public Integer getExp(String act, User user) {
        return template.queryForObject(SQL_GET_REWARD_EXP, Integer.class, user.getTier(), act);
    }

    public Map<String, Integer> getGatekeepRewards(String act, User user) {
        SqlRowSet rs = template.queryForRowSet(SQL_GET_GATEKEEP_REWARD, user.getTier(), act);
        if (rs.next()) {
            Map<String, Integer> rewardMap = new HashMap<>();
            rewardMap.put("exp", rs.getInt("exp"));
            rewardMap.put("credit", rs.getInt("credit"));
            return rewardMap;
        } else {
            return null;
        }
    }

    public SqlRowSet getGatekeepData() {
        return template.queryForRowSet(SQL_GET_GATEKEEP_DATA);
    }

    public SqlRowSet getNonGatekeepData() {
        return template.queryForRowSet(SQL_GET_NONGATEKEEP_DATA);
    }

}
