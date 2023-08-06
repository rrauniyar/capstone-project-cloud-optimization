package com.capstone.beginsetup.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.capstone.beginsetup.model.AWSCredentialsDto;
import com.capstone.beginsetup.model.EC2InstanceInfoSeven;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Date;
import java.text.DecimalFormat;
import java.util.List;
@RestController
@RequestMapping("/ec2-instances-seven")
public class EC2ControllerCPUSeven {
   
    private AmazonEC2 ec2Client;

    private AmazonCloudWatch cloudWatchClient;


    @PostMapping("/configure")
    public ResponseEntity<String> configureAWS(@RequestBody AWSCredentialsDto credentialsRequest) {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(credentialsRequest.getAccessKey(), credentialsRequest.getSecretKey());

            // Configure Amazon EC2
           ec2Client = AmazonEC2ClientBuilder.standard()
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
    @GetMapping("/seven-day")
    public ResponseEntity<?> getAllEC2InstanceInfo() {
        try{
           DecimalFormat decimalFormat = new DecimalFormat("#.00");
        List<EC2InstanceInfoSeven> instanceInfoList = new ArrayList<>();
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest()
                .withFilters(new Filter("instance-state-name").withValues("running"));
        DescribeInstancesResult describeInstancesResult = ec2Client.describeInstances(describeInstancesRequest);
        List<Reservation> reservations = describeInstancesResult.getReservations();
        for (Reservation reservation : reservations) {
            List<Instance> instances = reservation.getInstances();
            for (Instance instance : instances) {
                String instanceId = instance.getInstanceId();
                String instanceType = instance.getInstanceType();
                String instanceName = getInstanceName(instance);
                double memoryInGib = getInstanceMemoryInGiB(instanceType);
                double averageCpuUtilization = getAverageCpuUtilization(instanceId,7);
                double  totalCostForSevenDays = getTotalCostForSevenDays(instanceType,7);

                String formattedAverageCpuUtilization = decimalFormat.format(averageCpuUtilization);
        String formattedTotalCostForSevenDays = decimalFormat.format(totalCostForSevenDays);

        EC2InstanceInfoSeven instanceInfo = new EC2InstanceInfoSeven(
            instanceId,
            instanceName,
            instanceType,
            instance.getCpuOptions().getThreadsPerCore() * instance.getCpuOptions().getCoreCount(),
            memoryInGib,
            Double.parseDouble(formattedAverageCpuUtilization),
            Double.parseDouble(formattedTotalCostForSevenDays));
        
        instanceInfoList.add(instanceInfo);
    }
        }
        return new ResponseEntity<>(instanceInfoList, HttpStatus.OK);
    
        }catch (AmazonServiceException e) {
            return new ResponseEntity<>("Failed to fetch EC2 instance information. Please check your AWS credentials and configuration.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to fetch EC2 instance information.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
        
    private String getInstanceName(Instance instance) {
        for (Tag tag : instance.getTags()) {
            if (tag.getKey().equals("Name")) {
                return tag.getValue();
            }
        }
        return "";
    }
    private double getInstanceMemoryInGiB(String instanceType) {
        DescribeInstanceTypesRequest describeInstanceTypesRequest = new DescribeInstanceTypesRequest()
                .withInstanceTypes(instanceType);
        DescribeInstanceTypesResult describeInstanceTypesResult = ec2Client.describeInstanceTypes(describeInstanceTypesRequest);
        List<InstanceTypeInfo> instanceTypes = describeInstanceTypesResult.getInstanceTypes();
        if (!instanceTypes.isEmpty()) {
            InstanceTypeInfo instanceTypeInfo = instanceTypes.get(0);
            MemoryInfo memoryInfo = instanceTypeInfo.getMemoryInfo();
            return memoryInfo.getSizeInMiB() / 1024.0; // Convert from MiB to GiB
        }
        return 0.0;
    }
    private double getAverageCpuUtilization(String instanceId, int days) {
        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
            .withNamespace("AWS/EC2")
            .withMetricName("CPUUtilization")
            .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
            .withStartTime(new Date(System.currentTimeMillis() - days * 24 * 60 * 60 * 1000)) // Start time: N days ago
            .withEndTime(new Date()) // End time: current time
            .withPeriod(900) // 15-minute interval
            .withStatistics("Average")
            .withUnit("Percent");
        GetMetricStatisticsResult result = cloudWatchClient.getMetricStatistics(request);
        List<Datapoint> dataPoints = result.getDatapoints();
        if (!dataPoints.isEmpty()) {
            double totalCpuUtilization = 0.0;
            for (Datapoint dataPoint : dataPoints) {
                totalCpuUtilization += dataPoint.getAverage();
            }
            return totalCpuUtilization / dataPoints.size();
        }
        return 0.0;
    }
    private double getTotalCostForSevenDays(String instanceType,int days) {
        double costPerHour;
        switch (instanceType) {
            case "t2.nano":
            costPerHour = 0.0058;
            break;
            case "t2.micro":
                costPerHour = 0.0116;
                break;
            case "t2.small":
                costPerHour = 0.023;
                break;
            case "t2.medium":
                costPerHour = 0.0464;
                break;
            case "t3.micro":
                costPerHour = 0.0104;
                break;
            case "t3.2xlarge":
                costPerHour=0.3328;
                break;
            default:
                costPerHour = 0.0; // Default cost per hour for unknown instance types
        }
        double hoursPerDay = 24.0; // Total hours in a day
        double totalDays = (double) days; // Total days for cost calculation
        double totalCost = costPerHour * hoursPerDay * totalDays;
        return totalCost;
    }
}
