package com.usermanagement.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.usermanagement.exception.GlobalExceptionHandler;

@TestConfiguration
@ComponentScan(
    basePackages = "com.usermanagement.exception",
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalExceptionHandler.class)
)
public class ControllerTestConfig {} 