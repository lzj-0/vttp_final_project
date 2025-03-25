package com.recipes.FlavourFoundry.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.recipes.FlavourFoundry.model.Recipe;
import com.recipes.FlavourFoundry.model.User;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${do.storage.bucket}")
    private String bucketName;

    @Value("${do.storage.endpoint}")
    private String endPoint;

    public String uploadProfilePhoto(MultipartFile file, User user) throws IOException {
        Map<String, String> metadata = Map.of(
            "userId", user.getId(),
            "email", user.getEmail()
        );

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setUserMetadata(metadata);

        String finalFilename = "profile/" + user.getId() + ".jpg";

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, finalFilename, file.getInputStream(), objectMetadata);
        putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3.putObject(putObjectRequest);
        return "https://%s.%s/%s".formatted(bucketName, endPoint, finalFilename);
    }

    public String uploadRecipePhoto(MultipartFile file, Recipe recipe) throws IOException {
        Map<String, String> metadata = Map.of(
            "userId", recipe.getId(),
            "email", recipe.getEmail()
        );

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setUserMetadata(metadata);

        String finalFilename = "recipe/" + recipe.getId() + ".jpg";

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, finalFilename, file.getInputStream(), objectMetadata);
        putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3.putObject(putObjectRequest);
        return "https://%s.%s/%s".formatted(bucketName, endPoint, finalFilename);
    }

    public S3Object getImageBytesFromRecipe(String recipeId) throws IOException {
        GetObjectRequest getReq = new GetObjectRequest(bucketName, "recipe/%s.jpg".formatted(recipeId));
        return amazonS3.getObject(getReq);
    }
}
