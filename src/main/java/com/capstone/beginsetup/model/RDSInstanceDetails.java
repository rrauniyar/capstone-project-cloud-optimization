package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RDSInstanceDetails {
    private String dbInstanceID;
    private String instanceClass;
    private int vCPU;
    private int ramInGb;
    private String engineName;
    private double cpuUtilizationPerDay;
    private double totalCostPerDay;

}
