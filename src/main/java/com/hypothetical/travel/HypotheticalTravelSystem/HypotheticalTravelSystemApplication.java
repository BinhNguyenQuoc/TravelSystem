package com.hypothetical.travel.HypotheticalTravelSystem;

import com.hypothetical.travel.HypotheticalTravelSystem.service.SolutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class HypotheticalTravelSystemApplication {
    private static final Logger logger = LoggerFactory.getLogger(HypotheticalTravelSystemApplication.class);

    public static void main(String[] args) throws Exception {
        logger.info("Start the HypotheticalTravelSystemApplication");
        ConfigurableApplicationContext context = SpringApplication.run(HypotheticalTravelSystemApplication.class, args);

        // Get the service bean from the context
        SolutionService solutionService = context.getBean(SolutionService.class);

        // Invoke the service method
        solutionService.process();

        // Close the context when done (optional)
        context.close();
    }

}
