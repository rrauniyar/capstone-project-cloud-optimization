package com.capstone.beginsetup.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EC2InstanceDays {
    private  String instanceId;
    private String instanceName;
    private String instanceTypeName;
    private Integer vCPU;
    private Double memoryInGib;
    private Double averageCpuUtilization;
    private Double totalCost;
}
