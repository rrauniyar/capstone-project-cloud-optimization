package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceTypeData {
   
	private String instanceType;
    private double memoryInGB;
    private double costPerHour;
    private double costPerMonth;
    private double costPerYear;
    private int vCpus;
   

    private double CostPerVCpu;
    private double CostPerMemory;
  
}
