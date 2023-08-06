package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceTypeDtos {

   // private String instanceId;
    private  String instanceType;
    private int vCpus;
    private int ramSizeInGb;


}