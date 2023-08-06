package com.capstone.beginsetup.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.capstone.beginsetup.model.AWSCredentialsDto;
import com.capstone.beginsetup.model.InstanceTypeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
public class InstanceController {
   
    private AmazonEC2 ec2Client;




    @PostMapping("/configure/all")
    public ResponseEntity<String> configureAWS(@RequestBody AWSCredentialsDto credentialsRequest) {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(credentialsRequest.getAccessKey(), credentialsRequest.getSecretKey());

            // Configure Amazon EC2
           ec2Client = AmazonEC2ClientBuilder.standard()
                    .withRegion(credentialsRequest.getRegion())
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();


            return new ResponseEntity<>("AWS credentials configured successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to configure AWS credentials.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/instances")
    public List<InstanceTypeData> getInstancesDetails() {
        // Retrieve all instance types
        DescribeInstanceTypesRequest describeInstanceTypesRequest = new DescribeInstanceTypesRequest();
        DescribeInstanceTypesResult describeInstanceTypesResult = ec2Client.describeInstanceTypes(describeInstanceTypesRequest);
        List<InstanceTypeInfo> instanceTypes = describeInstanceTypesResult.getInstanceTypes();
        Map<String, InstanceTypeData> instanceTypeDataMap = new HashMap<>();
        // Iterate over each instance type
        for (InstanceTypeInfo instanceTypeInfo : instanceTypes) {
            String instanceType = instanceTypeInfo.getInstanceType();
            // Retrieve the pricing information for the instance type
            DescribeSpotPriceHistoryRequest describeSpotPriceHistoryRequest = new DescribeSpotPriceHistoryRequest();
            describeSpotPriceHistoryRequest.withInstanceTypes(instanceType);
            DescribeSpotPriceHistoryResult describeSpotPriceHistoryResult = ec2Client.describeSpotPriceHistory(describeSpotPriceHistoryRequest);
            List<SpotPrice> spotPrices = describeSpotPriceHistoryResult.getSpotPriceHistory();
            if (spotPrices.size() > 0) {
                SpotPrice spotPrice = spotPrices.get(0);
                // Calculate the cost per hour for the instance
                double costPerHour = Double.parseDouble(spotPrice.getSpotPrice());
                double costPerDay = costPerHour * 24.0;
                double costPerMonth = costPerDay * 30.0;
                double costPerYear = costPerMonth * 12.0;
                // Retrieve the vCPU and memory information for the instance type
                int vCpus = instanceTypeInfo.getVCpuInfo().getDefaultVCpus();
                int memory = Math.toIntExact(instanceTypeInfo.getMemoryInfo().getSizeInMiB());
                double memoryInGb = memory / 1024.0;
                InstanceTypeData instanceTypeData = new InstanceTypeData(
                        instanceType,
                        memoryInGb,
                        costPerHour,
                        costPerMonth,
                        costPerYear,
                        vCpus,
                        costPerHour / vCpus,
                        costPerHour / memory
                );
                instanceTypeDataMap.put(instanceType, instanceTypeData);
            }
        }
        // Convert the map to a list
        List<InstanceTypeData> instanceTypeDataList = new ArrayList<>(instanceTypeDataMap.values());
        // Return the list of instance type data
        return instanceTypeDataList;
    }
}