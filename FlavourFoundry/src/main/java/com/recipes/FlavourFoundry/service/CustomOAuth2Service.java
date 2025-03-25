package com.recipes.FlavourFoundry.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.recipes.FlavourFoundry.model.User;
import com.recipes.FlavourFoundry.repository.UserRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomOAuth2Service extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    UserRepo userRepo;

    @Autowired
    JWTService jwtService;


    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        
        // Check if user exists
        Optional<User> userOptional = userRepo.findByEmail(email);
        System.out.println("OAUTH");
        
        if (userOptional.isPresent()) {
            // User exists, redirect to home page
            try {
                System.out.println("Generating token");
                String token = jwtService.generateToken(email);
                String userId = userRepo.getIdByEmail(email);

                String redirectUrl = "/oauth2/callback" +
                // String redirectUrl = "http://localhost:4200/oauth2/callback" +
                "?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8.toString()) +
                "&token=" + URLEncoder.encode(token, StandardCharsets.UTF_8.toString()) +
                "&userId=" + URLEncoder.encode(userId, StandardCharsets.UTF_8.toString());

                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                
            } catch(Exception e) {
                e.printStackTrace();

                // getRedirectStrategy().sendRedirect(request, response, "http://localhost:4200/login?error=true");
                getRedirectStrategy().sendRedirect(request, response, "/login?error=true");
            }
        } else {

            String redirectUrl = "/register" +
            // String redirectUrl = "http://localhost:4200/register" +
                    "?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8.toString()) +
                    "&name=" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString()) +
                    "&picture=" + URLEncoder.encode(picture, StandardCharsets.UTF_8.toString());

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }

}
