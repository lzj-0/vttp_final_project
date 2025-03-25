package com.recipes.FlavourFoundry.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {
    
    @Value("${do.storage.key}")
    private String accessKey;

    @Value("${do.storage.secret}")
    private String secretKey;

    @Value("${do.storage.endpoint}")
    private String endpoint;

    @Value("${do.storage.region}")
    private String endPointRegion;

    @Bean 
    public AmazonS3 createS3Client() {
        BasicAWSCredentials awsCred = new BasicAWSCredentials(accessKey, secretKey);
        EndpointConfiguration endpointConfig = new EndpointConfiguration(endpoint, endPointRegion);

        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpointConfig)
                .withCredentials(new AWSStaticCredentialsProvider(awsCred))
                .build();
    }
}
