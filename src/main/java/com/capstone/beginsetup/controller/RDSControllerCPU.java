package com.capstone.beginsetup.controller;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.*;
import com.capstone.beginsetup.model.AWSCredentialsDto;
import com.capstone.beginsetup.model.RDSInstanceDetails;
import java.text.DecimalFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/rds-instances")
public class RDSControllerCPU {
   
 
private AmazonRDS rdsClient;
    private AmazonCloudWatch cloudWatchClient;


    @PostMapping("/configure")
    public ResponseEntity<String> configureAWS(@RequestBody AWSCredentialsDto credentialsRequest) {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(credentialsRequest.getAccessKey(), credentialsRequest.getSecretKey());

        // Configure Amazon RDS
           rdsClient  = AmazonRDSClientBuilder.standard()
                    .withRegion(credentialsRequest.getRegion())
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();
                    
    // Configure Amazon CloudWatch
            cloudWatchClient = AmazonCloudWatchClientBuilder.standard()
                    .withRegion(credentialsRequest.getRegion())
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();

            return new ResponseEntity<>("AWS credentials configured successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to configure AWS credentials.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping
    public ResponseEntity<?> getRDSInstancesDetails() {
       try{
 DecimalFormat decimalFormat = new DecimalFormat("#.00");
        List<RDSInstanceDetails> rdsInstances = new ArrayList<>();
        DescribeDBInstancesRequest request = new DescribeDBInstancesRequest();
        DescribeDBInstancesResult result = rdsClient.describeDBInstances(request);
        for (DBInstance dbInstance : result.getDBInstances()) {
            String dbInstanceID = dbInstance.getDBInstanceIdentifier();
            String instanceClass = dbInstance.getDBInstanceClass();
            int vCPU = getVCPUCountFromInstanceType(instanceClass);
            String engineName = dbInstance.getEngine();
            Integer ramInGb =  getRAMSizeFromInstanceClass(instanceClass);
            Double cpuUtilizationPerDay = getCpuUtilizationPerDay(dbInstanceID);
            Double totalCostPerDay = getTotalCostPerDay(instanceClass);
            String formattedCpuUtilizationPerDay = decimalFormat.format(cpuUtilizationPerDay);
            String formattedTotalCostPerDay = decimalFormat.format(totalCostPerDay);
            RDSInstanceDetails instanceDetails = new RDSInstanceDetails(dbInstanceID, instanceClass, vCPU, ramInGb,
            engineName, Double.parseDouble(formattedCpuUtilizationPerDay), Double.parseDouble(formattedTotalCostPerDay));
    rdsInstances.add(instanceDetails);
        }
         return new ResponseEntity<>(rdsInstances, HttpStatus.OK);
    }
    catch (AmazonClientException e) {
        return new ResponseEntity<>("Failed to fetch RDS instance information. Please check your AWS credentials.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
        return new ResponseEntity<>("Failed to fetch RDS instance information.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
       
       
    private int getVCPUCountFromInstanceType(String instanceType) {
        if (instanceType.equals("db.t2.micro")) {
            return 1;
        } else if (instanceType.equals("db.t2.small")) {
            return 1;
        } else if (instanceType.equals("db.t2.medium")) {
            return 2;
        } else if (instanceType.equals("db.t2.large")) {
            return 2;
        } else if (instanceType.equals("db.t3.micro")) {
            return 2;
        } else if (instanceType.equals("db.t3.small")) {
            return 2;
        } else if (instanceType.equals("db.t3.medium")) {
            return 2;
        } else if (instanceType.equals("db.t3.large")) {
            return 2;
        } else if (instanceType.equals("db.m5.large")) {
            return 2;
        }
        // Add more mappings for other instance types as needed
        return 0; // Default value if instance type is not found
    }
    private int getRAMSizeFromInstanceClass(String instanceClass) {
        // Map the instance class to the corresponding RAM size in GB
        // You can find the mapping in the AWS RDS documentation
        if (instanceClass.equals("db.t2.micro")) {
            return 1;
        } else if (instanceClass.equals("db.t2.small")) {
            return 2;
        } else if (instanceClass.equals("db.t2.medium")) {
            return 4;
        } else if (instanceClass.equals("db.t2.large")) {
            return 8;
        } else if (instanceClass.equals("db.t3.micro")) {
            return 1;
        } else if (instanceClass.equals("db.t3.small")) {
            return 2;
        } else if (instanceClass.equals("db.t3.medium")) {
            return 4;
        } else if (instanceClass.equals("db.t3.large")) {
            return 8;
        } else if (instanceClass.equals("db.m5.large")) {
            return 8;
        }
        // Add more mappings for other instance classes as needed
        return 0; // Default value if instance class is not found
    }
    private Double getCpuUtilizationPerDay(String dbInstanceID) {
        long startTimeMillis = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // One day ago
        long endTimeMillis = System.currentTimeMillis();
        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
                .withNamespace("AWS/RDS")
                .withMetricName("CPUUtilization")
                .withStartTime(new Date(startTimeMillis))
                .withEndTime(new Date(endTimeMillis))
                .withPeriod(60) // Granularity in seconds (one data point per minute)
                .withStatistics(Statistic.Average)
                .withUnit("Percent"); // Use Average statistic for CPU utilization
        // Specify the RDS instance dimension
        Dimension dimension = new Dimension()
                .withName("DBInstanceIdentifier")
                .withValue(dbInstanceID);
        request.withDimensions(dimension);
        // Get the CPU utilization data points
        GetMetricStatisticsResult result = cloudWatchClient.getMetricStatistics(request);
        List<Datapoint> dataPoints = result.getDatapoints();
        // Calculate the average CPU utilization per day
        double totalCpuUtilization = 0.0;
        for (Datapoint dataPoint : dataPoints) {
            totalCpuUtilization += dataPoint.getAverage();
        }
        double avgCpuUtilizationPerDay = totalCpuUtilization / dataPoints.size();
        return avgCpuUtilizationPerDay;
    }
    private Double getTotalCostPerDay(String dbInstanceID) {
        double hourlyCost;
        switch (dbInstanceID) {
            case "db.t2.micro":
                hourlyCost = 1.0;
                break;
            case "db.t2.small":
                hourlyCost = 2.0;
                break;
            case "db.t2.medium":
                hourlyCost = 3.0;
                break;
                case "db.t3.micro":
                hourlyCost=0.017;
                break;
            default:
               hourlyCost=0.0;
        }
        int hoursPerDay = 24;
        double totalCostPerDay = hourlyCost * hoursPerDay;
        return totalCostPerDay;
    }
}