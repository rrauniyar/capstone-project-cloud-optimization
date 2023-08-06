package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BucketDetailsUsage {
    private String bucketName;
    private double totalSizeGb;
    private double UsageSizeGb;
    private  double costPerMonth;
    private String StorageClass;
    private long StorageObjectCount;

    List<String> objectNames;
    private List<BucketDetailsUsage.ObjectDetails> objectDetailsList;


    public static class ObjectDetails {
        private String objectName;
        private long objectSize;

        // getters and setters

        public String getObjectName() {
            return objectName;
        }

        public void setObjectName(String objectName) {
            this.objectName = objectName;
        }

        public long getObjectSize() {
            return objectSize;
        }

        public void setObjectSize(long objectSize) {
            this.objectSize = objectSize;
        }
    }
}