// package com.capstone.beginsetup.config;



// import com.amazonaws.auth.AWSCredentials;
// import com.amazonaws.auth.AWSStaticCredentialsProvider;
// import com.amazonaws.auth.BasicAWSCredentials;
// import com.amazonaws.regions.Regions;
// import com.amazonaws.services.ec2.AmazonEC2;
// import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;

// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// @Data
// @AllArgsConstructor
// @NoArgsConstructor
// public class AmazonEC2Config {
//     @Value("${aws.access.key}")
//     private String accessKey;

//     @Value("${aws.secret.key}")
//     private String secretKey;
//     public void setCredentials(String accessKey, String secretKey) {
//         this.accessKey = accessKey;
//         this.secretKey = secretKey;
//     }
//     // public String callMethod(){
//     //  return accessKey;
//     // }
//     @Bean
//     public AmazonEC2 amazonEC2Client() {
   

//         AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//         AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
//                 .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                 .withRegion(Regions.EU_NORTH_1)
//                 .build();
//            return ec2;
//     }
// }