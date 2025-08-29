package com.example.webhooksolver.service;

import com.example.webhooksolver.dto.GenerateWebhookRequest;
import com.example.webhooksolver.dto.GenerateWebhookResponse;
import com.example.webhooksolver.dto.SolutionResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookSolverService {
    
    private final RestTemplate restTemplate;
    
    public WebhookSolverService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    
    public String executeWorkflowManually(String regNo) {
        try {
            // Step 1: Generate webhook
            GenerateWebhookResponse webhookResponse = generateWebhook();
            
            if (webhookResponse != null) {
                // Step 2: Solve the SQL problem based on regNo
                String finalQuery = solveSqlProblem(regNo);
                
                // Step 3: Submit solution
                String result = submitSolutionAndGetResponse(webhookResponse.getWebhook(), 
                                                           webhookResponse.getAccessToken(), 
                                                           finalQuery);
                return "Workflow executed successfully!\n" +
                       "Webhook: " + webhookResponse.getWebhook() + "\n" +
                       "Access Token: " + webhookResponse.getAccessToken() + "\n" +
                       "Final Query: " + finalQuery + "\n" +
                       "Result: " + result;
            }
            return "Failed to generate webhook";
        } catch (Exception e) {
            return "Error executing workflow: " + e.getMessage();
        }
    }
    
    public GenerateWebhookResponse generateWebhook() {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        
        GenerateWebhookRequest request = new GenerateWebhookRequest(
            "John Doe",
            "REG12347",
            "john@example.com"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<GenerateWebhookRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<GenerateWebhookResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, GenerateWebhookResponse.class);
            
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error generating webhook: " + e.getMessage(), e);
        }
    }
    
    private String solveSqlProblem(String regNo) {
        // Extract last two digits of registration number
        String lastTwoDigits = regNo.substring(regNo.length() - 2);
        int lastDigits = Integer.parseInt(lastTwoDigits);
        
        // Determine if odd or even
        if (lastDigits % 2 == 1) {
            // Odd - Question 1
            return solveQuestion1();
        } else {
            // Even - Question 2
            return solveQuestion2();
        }
    }
    
    private String solveQuestion1() {
        // Based on the Google Drive document for Question 1
        return "SELECT patient_id, patient_name, diagnosis, admission_date " +
               "FROM patients " +
               "WHERE diagnosis = 'Diabetes' " +
               "AND admission_date >= '2023-01-01' " +
               "ORDER BY admission_date DESC;";
    }
    
    private String solveQuestion2() {
        // Based on the Google Drive document for Question 2
        return "SELECT department, COUNT(*) as employee_count, AVG(salary) as avg_salary " +
               "FROM employees " +
               "WHERE join_date < '2023-01-01' " +
               "GROUP BY department " +
               "HAVING COUNT(*) > 5 " +
               "ORDER BY avg_salary DESC;";
    }
    
    public String submitSolutionAndGetResponse(String webhookUrl, String accessToken, String sqlQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        
        SolutionResponse solution = new SolutionResponse(sqlQuery);
        HttpEntity<SolutionResponse> entity = new HttpEntity<>(solution, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl, HttpMethod.POST, entity, String.class);
            
            return "Status: " + response.getStatusCode() + ", Body: " + response.getBody();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    public String testExternalApi(String url, String method, String body, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null && !authToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + authToken);
        }
        
        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, httpMethod, entity, String.class);
            
            return "Status: " + response.getStatusCode() + "\nHeaders: " + response.getHeaders() + "\nBody: " + response.getBody();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
