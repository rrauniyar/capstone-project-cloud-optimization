package com.capstone.beginsetup.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EC2InstanceInfoFive {
    private String instanceTypeName;
    private String instanceName;
    private Integer vCPU;
    private Double memoryInGib;
    private Double averageCpuUtilization;
    private Double totalCostForFiveDays;
}
