package com.hypothetical.travel.HypotheticalTravelSystem.service;

import com.hypothetical.travel.HypotheticalTravelSystem.model.TouchData;
import com.hypothetical.travel.HypotheticalTravelSystem.model.Trips;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolutionService {

    @Autowired
    CsvProcessorService fileService;

    @Autowired
    TripPrintService tripPrintService;

    @Autowired
    TripProcessor tripProcessor;

    private final Logger logger = LoggerFactory.getLogger(TripProcessor.class);

    public void process() throws Exception {
        List<TouchData> data = fileService.readFromCsv("touchData.csv");
        List<Trips> completedTrips = tripProcessor.getCompletedTrips(data);
        logger.info("The completed trips size: {}", completedTrips.size());

        List<Trips> unprocessedTrips = tripProcessor.getUnprocessedTrips(data);
        logger.info("The unprocessed trips size: {}", completedTrips.size());

        try {
            tripPrintService.printCompleteTrip(completedTrips);
        } catch (Exception ex) {
            logger.error("Error happens while printing the complete trips: {}", ex.getMessage(), ex);
        }

        try {
            tripPrintService.printUnprocessedTrip(unprocessedTrips);
        } catch (Exception ex) {
            logger.error("Error happens while printing the unprocessed trips: {}", ex.getMessage(), ex);
        }

        try {
            tripPrintService.printSummaryTrip(completedTrips, unprocessedTrips);
        } catch (Exception ex) {
            logger.error("Error happens while printing the summary trips: {}", ex.getMessage(), ex);
        }
    }
}
