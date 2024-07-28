package com.app.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class WebConfig {
    @Bean
    public ObjectMapper objectMapper(){
        return Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .build();
    }

    @Bean
    public XmlMapper xmlMapper() {
       XmlMapper xmlMapper =  new XmlMapper();
       xmlMapper.registerModule(new JavaTimeModule());
       xmlMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
       return xmlMapper;
    }
}
