package com.hypothetical.travel.HypotheticalTravelSystem.service;

import com.hypothetical.travel.HypotheticalTravelSystem.model.TouchData;
import com.hypothetical.travel.HypotheticalTravelSystem.model.TouchType;
import com.hypothetical.travel.HypotheticalTravelSystem.model.TripStatus;
import com.hypothetical.travel.HypotheticalTravelSystem.model.Trips;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hypothetical.travel.HypotheticalTravelSystem.utils.Utilities.hashingPAN;
import static com.hypothetical.travel.HypotheticalTravelSystem.utils.Utilities.isValidCreditCard;

@Service
public class SolutionService {

    @Autowired
    CsvProcessorService fileService;

    @Autowired
    TripPrintService tripPrintService;

    @Autowired
    TripCalculator tripCalculator;

    public void process() throws Exception {
        List<Trips> trips = new ArrayList<>();
        Map<String, Trips> map = new HashMap<>();
        fileService.readFromCsv("touchData.csv").forEach(touchData -> {
                    System.out.println(touchData);
                    if (touchData.getTouchType().equals(TouchType.ON.name())) {
                        handleTouchOn(touchData, map, trips);
                    } else if (touchData.getTouchType().equals(TouchType.OFF.name())) {
                        handleTouchOff(touchData, map);
                    }
                }

        );
        tripPrintService.printTrips(trips);
    }

    private void handleTouchOn(TouchData touchData, Map<String, Trips> map, List<Trips> trips) {
        Trips trip = new Trips();
        if (touchData.getStopID().isBlank() || touchData.getBusId().isBlank()) {
            trip.setStatus(TripStatus.MISSING_DATA.getValue());
        } else if (!isValidCreditCard(touchData.getPan())) {
            trip.setStatus(TripStatus.UNPROCESSED_NO_PAN.getValue());
        } else {
            trip.setStatus(TripStatus.INCOMPLETED.getValue());
        }
        trip.setStarted(touchData.getLocalDateTime());
        trip.setCompanyId(touchData.getCompanyId());
        trip.setBusId(touchData.getBusId());
        trip.setFromStopId(touchData.getStopID());

        trip.setChargeAmount(tripCalculator.calculateChargeAmount(trip.getFromStopId(), trip.getToStopId()));
        map.put(trip.getCompanyId(), trip);
        trips.add(trip);
    }

    private void handleTouchOff(TouchData touchData, Map<String, Trips> map) {
        Trips trip = map.get(touchData.getCompanyId());
        if (trip != null) {
            trip.setToStopId(touchData.getStopID());
            trip.setChargeAmount(tripCalculator.calculateChargeAmount(trip.getFromStopId(), trip.getToStopId()));
            trip.setFinished(touchData.getLocalDateTime());
            trip.setDurationSec(ChronoUnit.SECONDS.between(trip.getStarted(), touchData.getLocalDateTime()));
            if (trip.getFromStopId().equals(touchData.getStopID())) {
                trip.setStatus(TripStatus.CANCELLED.getValue());
            } else if (trip.getStarted().isAfter(touchData.getLocalDateTime()) || trip.getStarted().isEqual(touchData.getLocalDateTime())) {
                trip.setStatus(TripStatus.WRONG_DATE_TIME.getValue());
            } else if (!isValidCreditCard(touchData.getPan())) {
                trip.setStatus(TripStatus.UNPROCESSED_NO_PAN.getValue());
            } else {
                trip.setStatus(TripStatus.COMPLETED.getValue());
                trip.setHashedPan(hashingPAN(touchData.getPan()));
            }
        }
    }
}
