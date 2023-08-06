package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class S3BucketCost {

    //private double averageObjectDurationInMonths;
    private String bucketName;
    private double storageSizeInitial;
    private double storageSizeUsed;
    private BigDecimal costPerGBInitial;
    private String storageClass;
    private Date creationDate;
    private String objectName;
    private String objectType;
    private double objectSize;
    private Date lastModified;
}
