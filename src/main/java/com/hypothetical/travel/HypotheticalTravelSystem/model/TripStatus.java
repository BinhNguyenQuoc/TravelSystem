package com.hypothetical.travel.HypotheticalTravelSystem.model;

public enum TripStatus {
    INCOMPLETED("INCOMPLETED"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    UNPROCESSED_NO_PAN("Touch was missing PAN"),

    WRONG_DATE_TIME("Touch on time and touch off time are wrong"),

    MISSING_DATA("Data in missing");

    private final String tripStatus;

    TripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getValue() {
        return tripStatus;
    }
}
