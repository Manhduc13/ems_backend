package com.ndm.serve.configurations;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary getCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "ddfqvag5q");
        config.put("api_key", "362532191582845");
        config.put("api_secret", "ocBz1HuP2dVxPqfDN5Fye39xxIU");
        config.put("secure", true);
        return new Cloudinary(config);
    }
}
