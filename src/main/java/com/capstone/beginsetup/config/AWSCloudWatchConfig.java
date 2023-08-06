// package com.capstone.beginsetup.config;

// import com.amazonaws.auth.AWSCredentials;
// import com.amazonaws.auth.AWSStaticCredentialsProvider;
// import com.amazonaws.auth.BasicAWSCredentials;
// import com.amazonaws.regions.Regions;
// import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
// import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;

// import lombok.Data;

// import org.springframework.beans.factory.annotation.Value;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// @Data
// public class AWSCloudWatchConfig {
//     @Value("${aws.access.key}")
//     private String accessKey;

//     @Value("${aws.secret.key}")
//     private String secretKey;
//     public void setCredentials(String accessKey, String secretKey) {
//         this.accessKey = accessKey;
//         this.secretKey = secretKey;
//     }

// @Bean
//     public AmazonCloudWatch amazonCloudWatchaccessKey() {
   
//         AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//         AmazonCloudWatch amazonCloudWatch= AmazonCloudWatchClientBuilder.standard()
//                 .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                 .withRegion(Regions.EU_NORTH_1)
//                 .build();
//         return amazonCloudWatch;
//     }
  
// }
