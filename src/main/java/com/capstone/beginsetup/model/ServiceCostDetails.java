package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ServiceCostDetails {
    private String serviceName;
    private double costPerHour;
    private double costPerMonth;
   
        
}
