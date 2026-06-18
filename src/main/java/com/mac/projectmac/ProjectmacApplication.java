package com.mac.projectmac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ProjectmacApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectmacApplication.class, args);
    }

}
