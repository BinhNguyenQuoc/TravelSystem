package com.hypothetical.travel.HypotheticalTravelSystem.service;

import org.springframework.stereotype.Service;

@Service
public class TripCalculator {

    public double calculateChargeAmount(String startStopID, String endStopID) {
        String stopA = "StopA";
        String stopB = "StopB";
        String stopC = "StopC";
        if ((stopA.equals(startStopID) && stopB.equals(endStopID))
                || (stopB.equals(startStopID) && stopA.equals(endStopID))) {
            return 4.5;
        } else {
            if ((stopA.equals(startStopID) && (stopC.equals(endStopID) || endStopID == null))
                    || (stopC.equals(startStopID) && stopA.equals(endStopID))) {
                return 8.45;
            } else if ((stopB.equals(startStopID) && (stopC.equals(endStopID) || endStopID == null))
                    || (stopC.equals(startStopID) && stopB.equals(endStopID))) {
                return 6.25;
            }
        }
        return 0.0;
    }

}
