package com.example.springsocial;

import com.example.springsocial.config.AppProperties;
import com.example.springsocial.security.TokenAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class SpringSocialApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(SpringSocialApplication.class, args);

	}
 @Override
    public void run(String... args) throws Exception {


        System.out.println("Compiled successfully");

    }

}
