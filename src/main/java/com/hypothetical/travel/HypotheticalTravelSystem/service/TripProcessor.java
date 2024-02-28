package com.hypothetical.travel.HypotheticalTravelSystem.service;

import com.hypothetical.travel.HypotheticalTravelSystem.HypotheticalTravelSystemApplication;
import com.hypothetical.travel.HypotheticalTravelSystem.model.TouchData;
import com.hypothetical.travel.HypotheticalTravelSystem.model.TouchType;
import com.hypothetical.travel.HypotheticalTravelSystem.model.TripStatus;
import com.hypothetical.travel.HypotheticalTravelSystem.model.Trips;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.hypothetical.travel.HypotheticalTravelSystem.utils.Utilities.hashingPAN;
import static com.hypothetical.travel.HypotheticalTravelSystem.utils.Utilities.isValidCreditCard;

@Service
public class TripProcessor {

    private final Logger logger = LoggerFactory.getLogger(TripProcessor.class);

    @Autowired
    TripCalculator tripCalculator;

    private List<Trips> fullTrips = null;

    public List<Trips> getCompletedTrips(List<TouchData> dataFromCSV) {
        if (fullTrips == null) {
            buildFullTrips(dataFromCSV);
        }
        return fullTrips.stream()
                .filter(c -> c.getStatus().equals(TripStatus.COMPLETED.getValue())
                        || c.getStatus().equals(TripStatus.INCOMPLETED.getValue()))
                .toList();
    }

    public List<Trips> getUnprocessedTrips(List<TouchData> dataFromCSV) {
        if (fullTrips == null) {
            buildFullTrips(dataFromCSV);
        }
        return fullTrips.stream()
                .filter(c -> c.getStatus().equals(TripStatus.UNPROCESSED_NO_PAN.getValue())
                        || c.getStatus().equals(TripStatus.WRONG_DATE_TIME.getValue())
                        || c.getStatus().equals(TripStatus.MISSING_DATA.getValue())
                        || c.getStatus().equals(TripStatus.CANCELLED.getValue()))
                .toList();
    }

    private void buildFullTrips(List<TouchData> dataFromCSV) {
        logger.info("Start to build the full trip");
        List<Trips> trips = new ArrayList<>();
        Map<String, Trips> map = new HashMap<>();
        dataFromCSV
                .forEach(touchData -> {
                            if (touchData.getTouchType().equals(TouchType.ON.name())) {
                                handleTouchOn(touchData, map, trips);
                            } else if (touchData.getTouchType().equals(TouchType.OFF.name())) {
                                handleTouchOff(touchData, map);
                            }
                        }
                );

        fullTrips = trips.stream()
                .sorted(Comparator.comparing(Trips::getStarted)
                        .thenComparing(Trips::getCompanyId)
                        .thenComparing(Trips::getBusId))
                .toList();
        logger.info("Complete to build the full trip, with strip size {}", fullTrips.size());
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
        if (trips.stream().noneMatch(c -> c.getStarted().isEqual(trip.getStarted())
                && c.getCompanyId().equals(trip.getCompanyId()))) {
            trips.add(trip);
        }
    }

    private void handleTouchOff(TouchData touchData, Map<String, Trips> map) {
        Trips trip = map.get(touchData.getCompanyId());
        if (trip != null) {
            trip.setBusId(touchData.getBusId());
            trip.setToStopId(touchData.getStopID());
            trip.setChargeAmount(tripCalculator.calculateChargeAmount(trip.getFromStopId(), trip.getToStopId()));
            trip.setFinished(touchData.getLocalDateTime());
            trip.setDurationSec(ChronoUnit.SECONDS.between(trip.getStarted(), touchData.getLocalDateTime()));
            if (trip.getFromStopId().equals(touchData.getStopID())) {
                trip.setStatus(TripStatus.CANCELLED.getValue());
            } else if (trip.getStarted().isAfter(touchData.getLocalDateTime())
                    || trip.getStarted().isEqual(touchData.getLocalDateTime())) {
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
