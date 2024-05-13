package com.weighbridge.qualityuser.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

/**
 * This class is responsible for configuring the application's beans.
 */
public class AppConfig {

    /**
     * This method creates and returns an instance of ModelMapper bean.
     *
     * @return an instance of  ModelMapper
     */
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
