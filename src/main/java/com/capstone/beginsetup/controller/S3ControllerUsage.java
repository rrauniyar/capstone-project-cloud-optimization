package com.capstone.beginsetup.controller;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.capstone.beginsetup.model.AWSCredentialsDto;
import com.capstone.beginsetup.model.BucketDetailsUsage;
import com.capstone.beginsetup.model.BucketUsageByDateRange;
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
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
@RestController
@RequestMapping("/s3")
public class S3ControllerUsage {
// @Autowired
private AmazonS3 s3Client;
@PostMapping("/configure/usage")
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
    @GetMapping("/buckets-detailsusage")
    public ResponseEntity<?> getBucketDetailsUsage() {
       try{
         List<BucketDetailsUsage> bucketDetailsList = new ArrayList<>();
        List<Bucket> buckets = s3Client.listBuckets();
        double totalSizeOfAllBuckets = 0;
        for (Bucket bucket : buckets) {
            BucketDetailsUsage bucketDetails = new BucketDetailsUsage();
            bucketDetails.setBucketName(bucket.getName());
            try {
                ObjectListing objectListing = s3Client.listObjects(bucket.getName());
                List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
                long totalSize = 0;
                String storageClass = null;
                List<String> objectNames = new ArrayList<>();
                List<BucketDetailsUsage.ObjectDetails> objectDetailsList = new ArrayList<>();
                for (S3ObjectSummary objectSummary : objectSummaries) {
                    totalSize += objectSummary.getSize();
                    long objectSize = objectSummary.getSize();
                    BucketDetailsUsage.ObjectDetails objectDetails = new BucketDetailsUsage.ObjectDetails();
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
                double usageSize = gbSize - totalSizeOfAllBuckets;
                bucketDetails.setUsageSizeGb(usageSize);
                bucketDetails.setStorageClass(storageClass);
                bucketDetails.setStorageObjectCount(objectSummaries.size());
                bucketDetails.setObjectNames(objectNames);
                bucketDetails.setObjectDetailsList(objectDetailsList);
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
                bucketDetails.setCostPerMonth(costPerMonth);
                totalSizeOfAllBuckets += gbSize;
            } catch (AmazonServiceException e) {
                System.out.println("Error getting bucket usage: " + e.getMessage());
                bucketDetails.setTotalSizeGb(-1.0);
                bucketDetails.setUsageSizeGb(-1.0);
                bucketDetails.setCostPerMonth(-1.0);
                bucketDetails.setStorageClass("Unknown");
                bucketDetailsList.add(bucketDetails);
                bucketDetails.setStorageObjectCount(-1);
                continue;
            }
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
@GetMapping("/buckets-details/date-range-usage")
public ResponseEntity<?> getBucketDetailsDateRangeUsage(
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
    List<BucketUsageByDateRange> bucketDetailsList = new ArrayList<>();
    List<Bucket> buckets = s3Client.listBuckets();
    double totalSizeOfAllBuckets = 0;
    boolean dataFound = false;
    for (Bucket bucket : buckets) {
        BucketUsageByDateRange bucketDetails = new BucketUsageByDateRange();
        bucketDetails.setBucketName(bucket.getName());
        try {
            ObjectListing objectListing = s3Client.listObjects(bucket.getName());
            List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            long totalSize = 0;
            String storageClass = null;
            List<String> objectNames = new ArrayList<>();
            List<BucketUsageByDateRange.ObjectDetails> objectDetailsList = new ArrayList<>();
            for (S3ObjectSummary objectSummary : objectSummaries) {
                Date lastModified = objectSummary.getLastModified();
                LocalDate modifiedDate = lastModified.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (!modifiedDate.isBefore(startDate) && !modifiedDate.isAfter(endDate)) {
                    dataFound = true;
                    totalSize += objectSummary.getSize();
                    long objectSize = objectSummary.getSize();
                    BucketUsageByDateRange.ObjectDetails objectDetails = new BucketUsageByDateRange.ObjectDetails();
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
            double usageSize = gbSize - totalSizeOfAllBuckets;
            bucketDetails.setStartDate(startDate);
            bucketDetails.setEndDate(endDate);
            bucketDetails.setUsageSizeGb(usageSize);
            bucketDetails.setStorageClass(storageClass);
            bucketDetails.setStorageObjectCount(objectSummaries.size());
            bucketDetails.setObjectNames(objectNames);
            bucketDetails.setObjectDetailsList(objectDetailsList);

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
            bucketDetails.setCostPerMonth(costPerMonth);
            totalSizeOfAllBuckets += gbSize;
        } catch (AmazonServiceException e) {
            System.out.println("Error getting bucket usage: " + e.getMessage());
            bucketDetails.setTotalSizeGb(-1.0);
            bucketDetails.setUsageSizeGb(-1.0);
            bucketDetails.setCostPerMonth(-1.0);
            bucketDetails.setStorageClass("Unknown");
            bucketDetailsList.add(bucketDetails);
            bucketDetails.setStorageObjectCount(-1);
            continue;
        }
        bucketDetailsList.add(bucketDetails);
    }
    return ResponseEntity.ok(bucketDetailsList);
} catch (AmazonClientException e) {
        return new ResponseEntity<>("Failed to fetch S3 Bucket information. Please check your AWS credentials.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
        return new ResponseEntity<>("Failed to fetch S3 Bucket information.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
   }
}