package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceInfo {
    private String instanceTypeName;
    private Integer vCpu;
    private Integer memoryGb;
    private Float costPerCpu;
    private Float costPerMemoryGb;
    private Float costPerHour;
    private Float costPerMonth;
}
