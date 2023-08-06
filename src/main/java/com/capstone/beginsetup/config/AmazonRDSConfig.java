// package com.capstone.beginsetup.config;
// import com.amazonaws.auth.AWSCredentials;
// import com.amazonaws.auth.AWSStaticCredentialsProvider;
// import com.amazonaws.auth.BasicAWSCredentials;
// import com.amazonaws.regions.Regions;
// import com.amazonaws.services.rds.AmazonRDS;
// import com.amazonaws.services.rds.AmazonRDSClientBuilder;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// @Configuration
// public class AmazonRDSConfig {
//     @Value("${aws.access.key}")
//     private String accessKey;
//     @Value("${aws.secret.key}")
//     private String secretKey;
//     public void setCredentials(String accessKey, String secretKey) {
//         this.accessKey = accessKey;
//         this.secretKey = secretKey;
//     }
//     @Bean
//     public AmazonRDS amazonRDSClient() {
//         if (accessKey == null || secretKey == null) {
//             throw new IllegalArgumentException("Access key and secret key cannot be null");
//         }
//         AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//         AmazonRDS rds = AmazonRDSClientBuilder.standard()
//                 .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                 .withRegion(Regions.EU_NORTH_1)
//                 .build();
//         return rds;
//     }
// }