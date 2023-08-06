package com.capstone.beginsetup;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.capstone.beginsetup.model.AWSCredentialsDto;

@SpringBootApplication
public class BeginsetupApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BeginsetupApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


        System.out.println("Compiled successfully");

    }

}

















