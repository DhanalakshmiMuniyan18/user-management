package com.usermanagement.api.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceImplTest {
    @Test
    void contextLoads() {
        JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
        TemplateEngine templateEngine = Mockito.mock(TemplateEngine.class);
        EmailServiceImpl service = new EmailServiceImpl(mailSender, templateEngine);
        assertNotNull(service);
    }
}
