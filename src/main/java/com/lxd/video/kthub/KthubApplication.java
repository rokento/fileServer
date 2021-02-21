package com.lxd.video.kthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"com.lxd"})
public class KthubApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(KthubApplication.class, args);
    }

}
