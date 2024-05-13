package com.weighbridge.qualityuser.configs;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class is responsible for configuring the Cross-Origin Resource Sharing (CORS) for the application.
 * It implements the WebMvcConfigurer interface to override the default CORS configuration.
 */
public class CorsConfiguration implements WebMvcConfigurer {
    /**
     * Overrriden method to add CORS mapping to the application.
     *
     * @param corsRegistry object to configure CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // Specify the allowed origins.
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Specify the allowed HTTP methods.
                .allowedHeaders("*") // Allow all headers.
                .allowCredentials(true); // Allowed credentials.
    }
}
