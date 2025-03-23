package com.mgumussoy.advancedtaskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
})
@EnableJpaAuditing
public class AdvancedTaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvancedTaskManagementApplication.class, args);
    }

}
