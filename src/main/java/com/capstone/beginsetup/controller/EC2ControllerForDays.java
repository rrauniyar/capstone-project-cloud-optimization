// package com.capstone.beginsetup.controller;


// import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
// import com.amazonaws.services.cloudwatch.model.Datapoint;
// import com.amazonaws.services.cloudwatch.model.Dimension;
// import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
// import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
// import com.amazonaws.services.ec2.AmazonEC2;
// import com.amazonaws.services.ec2.model.DescribeInstanceTypesRequest;
// import com.amazonaws.services.ec2.model.DescribeInstanceTypesResult;
// import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
// import com.amazonaws.services.ec2.model.DescribeInstancesResult;
// import com.amazonaws.services.ec2.model.Filter;
// import com.amazonaws.services.ec2.model.Instance;
// import com.amazonaws.services.ec2.model.InstanceTypeInfo;
// import com.amazonaws.services.ec2.model.MemoryInfo;
// import com.amazonaws.services.ec2.model.Reservation;
// import com.capstone.beginsetup.model.EC2InstanceDays;
// import org.springframework.beans.factory.annotation.Autowired;
// import com.amazonaws.services.ec2.model.Tag;

// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.util.ArrayList;
// import java.util.Date;
// import java.util.List;

// @RestController
// @RequestMapping("/ec2-instances")
// public class EC2ControllerForDays {
//     @Autowired
//     private AmazonEC2 ec2Client;
//     @Autowired
//     private AmazonCloudWatch cloudWatchClient;

//     @GetMapping("/average-cpu-utilization/{numberOfDays}")
//     public List<EC2InstanceDays> getAverageCpuUtilizationForDays(@PathVariable int numberOfDays) {
//         List<EC2InstanceDays> instanceInfoList = new ArrayList<>();
//         Date endTime = new Date();
//         Date startTime = new Date(endTime.getTime() - numberOfDays * 24 * 60 * 60 * 1000);

//         DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest()
//                 .withFilters(new Filter("instance-state-name").withValues("running"));
//         DescribeInstancesResult describeInstancesResult = ec2Client.describeInstances(describeInstancesRequest);
//         List<Reservation> reservations = describeInstancesResult.getReservations();

//         for (Reservation reservation : reservations) {
//             List<Instance> instances = reservation.getInstances();

//             for (Instance instance : instances) {
//                 String instanceId = instance.getInstanceId();
//                 String instanceType = instance.getInstanceType();
//                 String instanceName = getInstanceName(instance);

//                 double memoryInGib = getInstanceMemoryInGiB(instanceType);
//                 double averageCpuUtilization = getAverageCpuUtilization(instanceId, startTime, endTime);
//                 double totalCost = getTotalCost(instanceType, numberOfDays);

//                 EC2InstanceDays instanceInfo = new EC2InstanceDays(
//                         instanceId,
//                         instanceName,
//                         instanceType,
//                         instance.getCpuOptions().getThreadsPerCore() * instance.getCpuOptions().getCoreCount(),
//                         memoryInGib,
//                         averageCpuUtilization,
//                         totalCost);

//                 instanceInfoList.add(instanceInfo);
//             }
//         }

//         return instanceInfoList;
//     }

//     private String getInstanceName(Instance instance) {
//         for (Tag tag : instance.getTags()) {
//             if (tag.getKey().equals("Name")) {
//                 return tag.getValue();
//             }
//         }
//         return "";
//     }

//     private double getInstanceMemoryInGiB(String instanceType) {
//         DescribeInstanceTypesRequest describeInstanceTypesRequest = new DescribeInstanceTypesRequest()
//                 .withInstanceTypes(instanceType);
//         DescribeInstanceTypesResult describeInstanceTypesResult = ec2Client.describeInstanceTypes(describeInstanceTypesRequest);
//         List<InstanceTypeInfo> instanceTypes = describeInstanceTypesResult.getInstanceTypes();

//         if (!instanceTypes.isEmpty()) {
//             InstanceTypeInfo instanceTypeInfo = instanceTypes.get(0);
//             MemoryInfo memoryInfo = instanceTypeInfo.getMemoryInfo();
//             return memoryInfo.getSizeInMiB() / 1024.0; // Convert from MiB to GiB
//         }

//         return 0.0;
//     }

//     private double getAverageCpuUtilization(String instanceId, Date startTime, Date endTime) {
//         GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
//                 .withNamespace("AWS/EC2")
//                 .withMetricName("CPUUtilization")
//                 .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
//                 .withStartTime(startTime)
//                 .withEndTime(endTime)
//                 .withPeriod(900)
//                 .withStatistics("Average")
//                 .withUnit("Percent");

//         GetMetricStatisticsResult result = cloudWatchClient.getMetricStatistics(request);
//         List<Datapoint> dataPoints = result.getDatapoints();

//         if (!dataPoints.isEmpty()) {
//             double totalCpuUtilization = 0.0;
//             for (Datapoint dataPoint : dataPoints) {
//                 totalCpuUtilization += dataPoint.getAverage();
//             }
//             return totalCpuUtilization / dataPoints.size();
//         }

//         return 0.0;
//     }

//     private double getTotalCost(String instanceType, int numberOfDays) {
//         // Calculate the total cost for the specified number of days based on the instance type
      
//         double costPerHour;
//         switch (instanceType) {
//             case "t2.nano":
//                 costPerHour = 0.0058;
//                 break;
//             case "t2.micro":
//                 costPerHour = 0.0116;
//                 break;
//             case "t2.small":
//                 costPerHour = 0.023;
//                 break;
//             case "t2.medium":
//                 costPerHour = 0.0464;
//                 break;
//             case "t3.micro":
//                 costPerHour = 0.0104;
//                 break;
//             default:
//                 costPerHour = 0.0; // Default cost per hour for unknown instance types
//         }

//         double hoursPerDay = 24.0; // Total hours in a day

//         double totalCost = costPerHour * hoursPerDay * numberOfDays;

//         return totalCost;
//     }
// }
