package com.example.template.config;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.template.common.dto.CustomLocalDateTimeDeserializer;
import com.example.template.common.dto.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;


/**
 * ObjectMapperConfig
 * - JSON 직렬화 클래스
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Configuration
public class ObjectMapperConfig {
	@Bean
    public ObjectMapper objectMapper() {
		SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer());
        simpleModule.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(simpleModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
