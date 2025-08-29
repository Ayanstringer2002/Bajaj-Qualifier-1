package com.example.webhooksolver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WebhookSolverApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WebhookSolverApplication.class, args);
        
        // Get the service bean and execute the workflow
        WebhookSolverService service = context.getBean(WebhookSolverService.class);
        service.executeWorkflow();
    }
}
