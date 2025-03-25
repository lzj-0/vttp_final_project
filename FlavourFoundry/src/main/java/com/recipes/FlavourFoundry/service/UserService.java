package com.recipes.FlavourFoundry.service;

import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.recipes.FlavourFoundry.model.User;
import com.recipes.FlavourFoundry.repository.UserRepo;

import io.jsonwebtoken.security.InvalidKeyException;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTService jwtService;

    @Autowired
    S3Service s3Service;

    @Value("${do.storage.bucket}")
    private String bucketName;

    @Value("${do.storage.endpoint}")
    private String endPoint;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    @Transactional
    public boolean registerUser(User user, MultipartFile file) throws IOException {
        System.out.println(user);

        user.setPassword(encoder.encode(user.getPassword()));
        String userId = userRepo.registerUser(user);
        user.setId(userId);
        String imageUrl;
        if (file != null) {
            imageUrl = s3Service.uploadProfilePhoto(file, user);
        } else {
            imageUrl = String.format("https://%s.%s/%s", bucketName, endPoint, "profile/default-profile.jpg");
        }
        userRepo.updateImage(user.getId(), imageUrl);

        return true;
    }

    public String verifyUser(User user) throws InvalidKeyException, NoSuchAlgorithmException {
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            if (auth.isAuthenticated()) {
                return jwtService.generateToken(user.getEmail());
            } else {
                return "fail";
            }
        } catch(BadCredentialsException e) {
            e.printStackTrace();
            System.out.println("Error");
            return "fail";
        }
    }

    public String getIdByEmail(String email) {
        return userRepo.getIdByEmail(email);
    }

    public JsonObject getUserById(String id) {
        Optional<User> opt = userRepo.getUserById(id);

        if (opt.isPresent()) {
            User user = opt.get();

            return Json.createObjectBuilder().add("id", user.getId())
                                            .add("email", user.getEmail())
                                            .add("name", user.getName())
                                            .add("imageUrl", user.getImageUrl())
                                            .add("level", user.getLevel())
                                            .add("exp", user.getExp())
                                            .add("credits", user.getCredits())
                                            .add("recipesTried", user.getRecipesTried())
                                            .add("gatekeepNo", user.getGatekeepNo())
                                            .add("walletAddress", (user.getWalletAddress() == null) ? "" : user.getWalletAddress())
                                            .add("tier", user.getTier())
                                            .add("isPremium", user.isPremium())
                                            .build();
        }

        return null;
    }

    public JsonObject getUserByEmail(String email) {
        Optional<User> opt = userRepo.findByEmail(email);

        if (opt.isPresent()) {
            User user = opt.get();

            return Json.createObjectBuilder().add("id", user.getId())
                                            .add("email", user.getEmail())
                                            .add("name", user.getName())
                                            .add("imageUrl", user.getImageUrl())
                                            .add("level", user.getLevel())
                                            .add("exp", user.getExp())
                                            .add("credits", user.getCredits())
                                            .add("recipesTried", user.getRecipesTried())
                                            .add("gatekeepNo", user.getGatekeepNo())
                                            .add("walletAddress", (user.getWalletAddress() == null) ? "" : user.getWalletAddress())
                                            .add("tier", user.getTier())
                                            .add("isPremium", user.isPremium())
                                            .build();
        }

        return null;
    }

    public String updateWallet(String payload, String email) {
        String walletAddress = Json.createReader(new StringReader(payload)).readObject().getString("walletAddress");

        if (userRepo.updateWallet(walletAddress, email)) {
            return walletAddress;
        } else {
            return null;
        }
    }

    public void updateUserPremiumStatus(String userId, boolean status) {
        userRepo.updateUserPremiumStatus(userId, status);
    }

    @Transactional
    public boolean transfer(String userIdFrom, String userIdTo, Integer amount) {
        
        boolean withdrawn = userRepo.withdrawCredit(userIdFrom, amount);
        boolean deposited = userRepo.depositCredit(userIdTo, amount);

        if (withdrawn && deposited) {
            return true;
        }

        throw new RuntimeException("Transfer of credits failed");
    }

    public Integer getGatekeepNo(String email) {
        return userRepo.getGateKeepNo(email);
    }

    public void updateGatekeepNo(String email, Integer n) {
        userRepo.updateGatekeepNo(email, n);
    }

    public void withdraw(String userId, Integer credits) {
        userRepo.withdrawCredit(userId, credits);
    }
}
