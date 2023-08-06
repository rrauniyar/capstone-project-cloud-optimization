package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BucketDetailsByDateRange {
    private String bucketName;
    private double totalSizeGb;
    private LocalDate startDate;
    private LocalDate endDate;
    // private double UsageSizeGb;
    private  double costPerMonth;
    private String StorageClass;
    private long StorageObjectCount;
    private String region;
   

    List<String> objectNames;
    private List<BucketDetailsByDateRange.ObjectDetails> objectDetailsList;
    
    


    public static class ObjectDetails {
        private String objectName;
        private long objectSize;
        private int numberOfDaysSinceLastAccess;
        private ZonedDateTime lastModifiedDate;
        // getters and setters
        public ZonedDateTime getLastModifiedDate() {
            return lastModifiedDate;
        }
    
        public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
        }
        public String getObjectName() {
            return objectName;
        }
        public void setNumberOfDaysSinceLastAccess(int numberOfDaysSinceLastAccess){
            this.numberOfDaysSinceLastAccess=numberOfDaysSinceLastAccess;
        }
        public int getNumberOfDaysSinceLastAccess(){
            return numberOfDaysSinceLastAccess;
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
