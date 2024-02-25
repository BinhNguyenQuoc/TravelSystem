package com.hypothetical.travel.HypotheticalTravelSystem;

import com.hypothetical.travel.HypotheticalTravelSystem.service.SolutionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class HypotheticalTravelSystemApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(HypotheticalTravelSystemApplication.class, args);

        // Get the service bean from the context
        SolutionService solutionService = context.getBean(SolutionService.class);

        // Invoke the service method
        solutionService.process();

        // Close the context when done (optional)
        context.close();
    }

}
