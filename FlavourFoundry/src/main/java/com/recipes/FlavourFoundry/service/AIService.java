package com.recipes.FlavourFoundry.service;

import java.io.IOException;

import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

@Service
public class AIService {

    @Autowired
    VertexAiGeminiChatModel chatModel;

    @Autowired
    GenerativeModel visionModel;

    @Autowired
    S3Service s3Service;

    public Integer getCalories(String payload) {
        
        String prompt = Prompts.GET_CALORIE.formatted(payload);

        String resp = chatModel.call(prompt);

        System.out.println("resp: " + resp);

        return Integer.parseInt(resp.trim());
    }

    public Integer compareImage(MultipartFile file, String recipeId) throws IOException {

		byte[] uploadedImage = file.getBytes();
        S3Object actualImageData = s3Service.getImageBytesFromRecipe(recipeId);
        S3ObjectInputStream is = actualImageData.getObjectContent();
        byte[] actualImage = is.readAllBytes();

        GenerateContentResponse response = visionModel.generateContent(
            ContentMaker.fromMultiModalData(
                Prompts.GET_SIMILARITY_SCORE,
                PartMaker.fromMimeTypeAndData(file.getContentType(), uploadedImage),
                PartMaker.fromMimeTypeAndData(actualImageData.getObjectMetadata().getContentType(), actualImage)
            )
        );

        
        is.close();
        
        return Integer.parseInt(ResponseHandler.getText(response).trim());
    }

    public String guessCalories(MultipartFile file) throws IOException {
        byte[] uploadedImage = file.getBytes();

        GenerateContentResponse response = visionModel.generateContent(
            ContentMaker.fromMultiModalData(
                Prompts.GUESS_CALORIE,
                PartMaker.fromMimeTypeAndData(file.getContentType(), uploadedImage)
            )
        );

        return ResponseHandler.getText(response);
    }

}
