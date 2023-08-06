package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceTypeInfo {
 
    private String instanceTypeName;


    private int vCPU;


    private int ramGB;

    
    private double costPerVCPU;

 
    private double costPerRAM;

  
    private double costPerHour;


    private double costPerMonth;
}
