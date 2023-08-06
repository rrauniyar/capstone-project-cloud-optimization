package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AWSCostReportDateRange {
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalCostPerHour;
    private double totalCostPerMonth;
    private double totalCostPerYear;

    private List<Map<String, Object>> costDetails;
}