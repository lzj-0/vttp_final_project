package com.recipes.FlavourFoundry.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.recipes.FlavourFoundry.model.User;

@Repository
public class UserRepo {

    @Autowired
    JdbcTemplate template;

    public static String SQL_FIND_USER = "select * from users where email = ?";
    public static String SQL_FIND_USER_ID = "select * from users where id = ?";
    public static String SQL_GET_ID_EMAIL = "select id from users where email = ?";
    public static String SQL_CREATE_USER = "insert into users (email, name, password) values (?, ?, ?)";
    public static String SQL_GET_LEVEL = "select level from level_tiers where min_exp <= ? and max_exp >= ?";
    public static String SQL_GET_TIER = "select tier from level_tiers where min_exp <= ? and max_exp >= ?";
    public static String SQL_UPDATE_WALLET = "update users set walletAddress = ? where email = ?";
    public static String SQL_UPDATE_PREMIUM = "update users set isPremium = ? where id = ?";
    public static String SQL_UPDATE_IMAGE = "update users set imageUrl = ? where id = ?";
    public static String SQL_UPDATE_RECIPESTRIED = "update users set recipesTried = recipesTried + 1 where email = ?";
    public static String SQL_UPDATE_EXP = "update users set exp = exp + ? where email = ?";
    public static String SQL_UPDATE_CREDIT = "update users set credits = credits + ? where email = ?";
    public static String SQL_GET_USER_LEVELEXP = "select level, exp from users where email = ?";
    public static String SQL_UPDATE_LEVEL = "update users set level = ? where email = ?";
    public static String SQL_GET_REWARDS = "select earnCredit, gatekeepNo from level_tiers where level = ?";
    public static String SQL_GET_LEVEL_EMAIL = "select level from users where email = ?";
    public static String SQL_ADD_REWARDS = "update users set credits = credits + ?, gatekeepNo = gatekeepNo + ? where email = ?";
    public static String SQL_GET_USER_TIEREXP = "select tier, exp from users where email = ?";
    public static String SQL_UPDATE_TIER = "update users set tier = ? where email = ?";
    public static String SQL_WITHDRAW_CREDIT = "update users set credits = credits - ? where id = ?";
    public static String SQL_DEPOSIT_CREDIT = "update users set credits = credits + ? where id = ?";
    public static String SQL_GET_GATEKEEP = "select gatekeepNo from users where email = ?";
    public static String SQL_UPDATE_GATEKEEP = "update users set gatekeepNo = gatekeepNo + ? where email = ?";


    public Optional<User> findByEmail(String email) {

        User user;

        try {
            user = template.queryForObject(SQL_FIND_USER, BeanPropertyRowMapper.newInstance(User.class), email);
        } catch(EmptyResultDataAccessException e) {
            user = null;
        }


        return Optional.ofNullable(user);
    }

    public String registerUser(User user) {
        template.update(SQL_CREATE_USER, user.getEmail(), user.getName(), user.getPassword());
        return getIdByEmail(user.getEmail());
    }

    public Integer getLevelByExp(Integer exp) {
        return template.queryForObject(SQL_GET_LEVEL, Integer.class, exp, exp);
    }

    public String getTierByExp(Integer exp) {
        return template.queryForObject(SQL_GET_TIER, String.class, exp, exp);
    }

    public String getIdByEmail(String email) {
        return template.queryForObject(SQL_GET_ID_EMAIL, Integer.class, email).toString();
    }

    public Optional<User> getUserById(String id) {

        User user;

        try {
            user = template.queryForObject(SQL_FIND_USER_ID, BeanPropertyRowMapper.newInstance(User.class), Integer.parseInt(id));
        } catch(EmptyResultDataAccessException e) {
            user = null;
        }
        // System.out.println(user);

        return Optional.ofNullable(user);
        
    }

    public boolean updateWallet(String walletAddress, String email) {
        int updated = template.update(SQL_UPDATE_WALLET, walletAddress, email);
        return updated > 0;
    }

    public void updateUserPremiumStatus(String userId, boolean status) {
        template.update(SQL_UPDATE_PREMIUM, status, Integer.parseInt(userId));
    }

    public void updateImage(String userId, String imageUrl) {
        template.update(SQL_UPDATE_IMAGE, imageUrl, Integer.parseInt(userId));
    }

    public void addRecipesTriedCount(String email) {
        template.update(SQL_UPDATE_RECIPESTRIED, email);
    }

    public void awardExp(String email, Integer expToAward) {
        template.update(SQL_UPDATE_EXP, expToAward, email);
    }


    public SqlRowSet getUserLevelExp(String email) throws Exception {
        SqlRowSet rs = template.queryForRowSet(SQL_GET_USER_LEVELEXP, email);
        if (rs.next()) {
            return rs;
        }
        throw new Exception("User not found");
    }

    public void updateLevel(String email, Integer newLevel) {
        template.update(SQL_UPDATE_LEVEL, newLevel, email);
    }

    public void addRewards(String email) {
        Integer newLevel = template.queryForObject(SQL_GET_LEVEL_EMAIL, Integer.class, email);
        SqlRowSet rs = template.queryForRowSet(SQL_GET_REWARDS, newLevel);
        if (rs.next()) {
            Integer earnCredit = rs.getInt("earnCredit");
            Integer gatekeepNo = rs.getInt("gatekeepNo");

            if (earnCredit > 0 || gatekeepNo > 0) {
                template.update(SQL_ADD_REWARDS, earnCredit, gatekeepNo, email);
            }

        }
    }

    public SqlRowSet getUserTierExp(String email) throws Exception {
        SqlRowSet rs = template.queryForRowSet(SQL_GET_USER_TIEREXP, email);
        if (rs.next()) {
            return rs;
        }
        throw new Exception("User not found");
    }

    public void upgradeTier(String email, String newTier) {
        template.update(SQL_UPDATE_TIER, newTier, email);
    }

    public boolean withdrawCredit(String userIdFrom, Integer amount) {
        return template.update(SQL_WITHDRAW_CREDIT, amount, userIdFrom) > 0;
    }

    public boolean depositCredit(String userIdTo, Integer amount) {
        return template.update(SQL_DEPOSIT_CREDIT, amount, userIdTo) > 0;
    }

    public Integer getGateKeepNo(String email) {
        return template.queryForObject(SQL_GET_GATEKEEP, Integer.class, email);
    }

    public void updateGatekeepNo(String email, Integer n) {
        template.update(SQL_UPDATE_GATEKEEP, n, email);
    }

    public void awardCredit(String email, Integer creditToAward) {
        template.update(SQL_UPDATE_CREDIT, creditToAward, email);
    }

}
