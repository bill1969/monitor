package com.viewstar.monitorfile;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@PropertySource(value = "application.properties", encoding = "UTF-8")
public class MonitorfileApplication   {
    public static void main(String[] args) {
        SpringApplication.run(MonitorfileApplication.class, args);
    }
}
