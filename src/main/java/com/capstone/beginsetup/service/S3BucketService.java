// package com.capstone.beginsetup.service;

// import com.amazonaws.services.s3.AmazonS3;
// import com.amazonaws.services.s3.model.Bucket;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.ArrayList;
// import java.util.List;
// @Service
// public class S3BucketService {
//     @Autowired
//     private AmazonS3 amazonS3;

//     public List<Bucket> listBucketsForAccount() {

//         List<Bucket> buckets = new ArrayList<>();
//         for (Bucket bucket : amazonS3.listBuckets()) {
//                 buckets.add(bucket);
//         }
//         return buckets;
//     }
// }
