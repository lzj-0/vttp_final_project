package com.recipes.FlavourFoundry.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.recipes.FlavourFoundry.repository.LevelRepo;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Service
public class LevelService {

    @Autowired
    LevelRepo levelRepo;

    public JsonObject getLevelStats(Integer level) throws Exception {
        Optional<SqlRowSet> opt = levelRepo.getLevelStats(level);

        if (opt.isPresent()) {
            SqlRowSet rs = opt.get();
            return Json.createObjectBuilder().add("level", rs.getInt("level"))
                                            .add("min_exp", rs.getInt("min_exp"))
                                            .add("max_exp", rs.getInt("max_exp"))
                                            .add("tier", rs.getString("tier"))
                                            .add("earnCredit", rs.getInt("earnCredit"))
                                            .add("gatekeepNo", rs.getInt("gatekeepNo"))
                                            .build();
        }
        
        throw new Exception("Unknown level");

    }

}
