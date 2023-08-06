package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EC2Instance {
    private   String instanceId;

    private String instanceType;
    private int vCPUs;
    private double memoryGiB;
    private double averageCPUUtilizationPerDay;
   
}