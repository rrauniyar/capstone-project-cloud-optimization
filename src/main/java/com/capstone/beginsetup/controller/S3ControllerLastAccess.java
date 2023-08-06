package com.capstone.beginsetup.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.capstone.beginsetup.model.AWSCredentialsDto;
import com.capstone.beginsetup.model.BucketDetails;
import com.capstone.beginsetup.model.BucketDetailsByDateRange;
import com.capstone.beginsetup.model.MonthlyBucketDetails;
import com.capstone.beginsetup.model.YearlyBucketDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;


@RestController
@RequestMapping("/s3")
public class S3ControllerLastAccess {

   private AmazonS3 s3Client;



@PostMapping("/configure/access")
    public ResponseEntity<String> configureAWS(@RequestBody AWSCredentialsDto credentialsRequest) {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(credentialsRequest.getAccessKey(), credentialsRequest.getSecretKey());

            // Configure Amazon S3
            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(credentialsRequest.getRegion())
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();
                     return new ResponseEntity<>("AWS credentials configured successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to configure AWS credentials.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @GetMapping("/buckets-details")
    public ResponseEntity<?> getBucketDetails() {

    try{
        List<BucketDetails> bucketDetailsList = new ArrayList<>();
        List<Bucket> buckets = s3Client.listBuckets();
    
        for (Bucket bucket : buckets) {
            String bucketRegion;
            try {
                bucketRegion = s3Client.getBucketLocation(bucket.getName());
            } catch (AmazonS3Exception e) {
                e.printStackTrace();
                continue;
            }
    
            BucketDetails bucketDetails = new BucketDetails();
            bucketDetails.setBucketName(bucket.getName());
            bucketDetails.setRegion(bucketRegion);
    
            ObjectListing objectListing = s3Client.listObjects(bucket.getName());
            List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            long totalSize = 0;
            String storageClass = null;
            List<String> objectNames = new ArrayList<>();
            List<BucketDetails.ObjectDetails> objectDetailsList = new ArrayList<>();
            long currentTimeMillis = System.currentTimeMillis();
            for (S3ObjectSummary objectSummary : objectSummaries) {
                totalSize += objectSummary.getSize();
                long objectSize = objectSummary.getSize();
                BucketDetails.ObjectDetails objectDetails = new BucketDetails.ObjectDetails();
                ObjectMetadata objectMetadata = s3Client.getObjectMetadata(bucket.getName(), objectSummary.getKey());
                Date lastAccessDate = objectMetadata.getLastModified();
                ZonedDateTime lastModifiedDateTime = lastAccessDate.toInstant().atZone(ZoneId.systemDefault());
                objectDetails.setLastModifiedDate(lastModifiedDateTime);
                long timeDifferenceMillis = currentTimeMillis - lastAccessDate.getTime();
                int numberOfDays = (int) (timeDifferenceMillis / (1000 * 60 * 60 * 24));
                objectDetails.setNumberOfDaysSinceLastAccess(numberOfDays);
                objectDetails.setObjectName(objectSummary.getKey());
                objectDetails.setObjectSize(objectSize);
                objectDetailsList.add(objectDetails);
    
                if (storageClass == null) {
                    storageClass = objectSummary.getStorageClass();
                } else if (!storageClass.equals(objectSummary.getStorageClass())) {
                    storageClass = "mixed";
                    break;
                }
                objectNames.add(objectSummary.getKey());
            }
    
            double gbSize = (double) totalSize / (1024 * 1024 * 1024);
            bucketDetails.setTotalSizeGb(gbSize);
    
            bucketDetails.setStorageClass(storageClass);
            bucketDetails.setStorageObjectCount(objectSummaries.size());
            bucketDetails.setObjectNames(objectNames);
            bucketDetails.setObjectDetailsList(objectDetailsList);
    
            double costPerMonth = calculateCostPerMonth(storageClass, gbSize);
            bucketDetails.setCostPerMonth(costPerMonth);
    
            bucketDetailsList.add(bucketDetails);
        }
    
        return new ResponseEntity<>(bucketDetailsList, HttpStatus.OK);
    }

    catch (AmazonClientException e) {
        return new ResponseEntity<>("Failed to fetch S3 Bucket information. Please check your AWS credentials.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
        return new ResponseEntity<>("Failed to fetch S3 Bucket information.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
    

    
    

private double getTotalSizeOfAllBuckets(List<BucketDetails> bucketDetailsList) {
    double totalSize = 0;
    for (BucketDetails bucketDetails : bucketDetailsList) {
        totalSize += bucketDetails.getTotalSizeGb();
    }
    return totalSize;
}

private double calculateCostPerMonth(String storageClass, double gbSize) {
    double costPerMonth = 0;
    switch (storageClass) {
        case "STANDARD":
            costPerMonth = gbSize * 0.023;
            break;
        case "INTELLIGENT_TIERING":
            costPerMonth = gbSize * 0.022;
            break;
        case "STANDARD_IA":
            costPerMonth = gbSize * 0.0125;
            break;
        case "ONEZONE_IA":
            costPerMonth = gbSize * 0.01;
            break;
        case "GLACIER":
            costPerMonth = gbSize * 0.004;
            break;
        case "DEEP_ARCHIVE":
            costPerMonth = gbSize * 0.00099;
            break;
        case "mixed":
        default:
            costPerMonth = -1;
    }
    return costPerMonth;
}


@GetMapping("/buckets-details/date-range")
public ResponseEntity<?> getBucketDetailsByDateRange(
    @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
) {
   try{
     LocalDate currentDate = LocalDate.now();
    if (endDate.isAfter(currentDate)) {
        String message = "The specified end date is in the future.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    if (endDate.isBefore(startDate)) {
        String message = "The specified end date is before the start date.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
    List<BucketDetailsByDateRange> bucketDetailsList = new ArrayList<>();
    List<Bucket> buckets = s3Client.listBuckets();
    boolean dataFound = false;

    for (Bucket bucket : buckets) {
        String bucketRegion;
        try {
            bucketRegion = s3Client.getBucketLocation(bucket.getName());
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
            continue;
        }

        BucketDetailsByDateRange bucketDetails = new BucketDetailsByDateRange();
        bucketDetails.setBucketName(bucket.getName());
        bucketDetails.setRegion(bucketRegion);

        ObjectListing objectListing = s3Client.listObjects(bucket.getName());
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        long totalSize = 0;
        String storageClass = null;
        List<String> objectNames = new ArrayList<>();
        List<BucketDetailsByDateRange.ObjectDetails> objectDetailsList = new ArrayList<>();
        long currentTimeMillis = System.currentTimeMillis();

        for (S3ObjectSummary objectSummary : objectSummaries) {
            Date lastModified = objectSummary.getLastModified();
            LocalDate modifiedDate = lastModified.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!modifiedDate.isBefore(startDate) && !modifiedDate.isAfter(endDate)) {
                dataFound = true;
                totalSize += objectSummary.getSize();
                long objectSize = objectSummary.getSize();
                BucketDetailsByDateRange.ObjectDetails objectDetails = new BucketDetailsByDateRange.ObjectDetails();
                ObjectMetadata objectMetadata = s3Client.getObjectMetadata(bucket.getName(), objectSummary.getKey());
                Date lastAccessDate = objectMetadata.getLastModified();
                ZonedDateTime lastModifiedDateTime = lastAccessDate.toInstant().atZone(ZoneId.systemDefault());
                objectDetails.setLastModifiedDate(lastModifiedDateTime);
                long timeDifferenceMillis = currentTimeMillis - lastAccessDate.getTime();
                int numberOfDays = (int) (timeDifferenceMillis / (1000 * 60 * 60 * 24));
                objectDetails.setNumberOfDaysSinceLastAccess(numberOfDays);
                objectDetails.setObjectName(objectSummary.getKey());
                objectDetails.setObjectSize(objectSize);
                objectDetailsList.add(objectDetails);

                if (storageClass == null) {
                    storageClass = objectSummary.getStorageClass();
                } else if (!storageClass.equals(objectSummary.getStorageClass())) {
                    storageClass = "mixed";
                    break;
                }
                objectNames.add(objectSummary.getKey());
            }
        }

        if (!dataFound) {
            // No information found for the specified date range
            String message = "No data available for the specified date range.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        double gbSize = (double) totalSize / (1024 * 1024 * 1024);
        bucketDetails.setTotalSizeGb(gbSize);
        bucketDetails.setStartDate(startDate);
        bucketDetails.setEndDate(endDate);
        bucketDetails.setStorageClass(storageClass);
        bucketDetails.setStorageObjectCount(objectSummaries.size());
        bucketDetails.setObjectNames(objectNames);
        bucketDetails.setObjectDetailsList(objectDetailsList);

        double costPerMonth = calculateCostPerMonth(storageClass, gbSize);
        bucketDetails.setCostPerMonth(costPerMonth);

        bucketDetailsList.add(bucketDetails);
    }

    return ResponseEntity.ok(bucketDetailsList);
}  catch (AmazonClientException e) {
        return new ResponseEntity<>("Failed to fetch S3 Bucket information. Please check your AWS credentials.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
        return new ResponseEntity<>("Failed to fetch S3 Bucket information.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
   }
   
}