package com.recipes.FlavourFoundry.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

@Configuration
public class AIConfig {

    @Value("${spring.ai.vertex.ai.gemini.project-id}")
    private String projectId;

    @Value("${spring.ai.vertex.ai.gemini.location}")
    private String location;

    @Bean
    public GenerativeModel generativeVisionModel() {
        VertexAI vertexAI = new VertexAI(projectId, location);
        return new GenerativeModel("gemini-pro-vision", vertexAI);
    }
}
