package com.hypothetical.travel.HypotheticalTravelSystem.service;

import com.hypothetical.travel.HypotheticalTravelSystem.model.TouchData;
import com.hypothetical.travel.HypotheticalTravelSystem.model.Trips;
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

    public void process() throws Exception {
        List<TouchData> data = fileService.readFromCsv("touchData.csv");
        List<Trips> completedTrips = tripProcessor.getCompletedTrips(data);
        List<Trips> unprocessedTrips = tripProcessor.getUnprocessedTrips(data);
        tripPrintService.printCompleteTrip(completedTrips);
        tripPrintService.printCompleteTrip(unprocessedTrips);
        tripPrintService.printSummaryTrip(completedTrips, unprocessedTrips);
    }
}
