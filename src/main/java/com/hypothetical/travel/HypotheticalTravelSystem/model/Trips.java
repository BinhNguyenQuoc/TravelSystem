package com.hypothetical.travel.HypotheticalTravelSystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Trips {

    private LocalDateTime started;

    private LocalDateTime finished;

    private long durationSec;

    private String fromStopId;

    private String toStopId;

    private double chargeAmount;

    private String companyId;

    private String busId;

    private String hashedPan;

    private String status;

    public LocalDateTime getStarted() {
        return started;
    }

    public LocalDate getDateOnly() {
        return started.toLocalDate();
    }

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public LocalDateTime getFinished() {
        return finished;
    }

    public void setFinished(LocalDateTime finished) {
        this.finished = finished;
    }

    public long getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(long durationSec) {
        this.durationSec = durationSec;
    }

    public String getFromStopId() {
        return fromStopId;
    }

    public void setFromStopId(String fromStopId) {
        this.fromStopId = fromStopId;
    }

    public String getToStopId() {
        return toStopId;
    }

    public void setToStopId(String toStopId) {
        this.toStopId = toStopId;
    }

    public double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getHashedPan() {
        return hashedPan;
    }

    public void setHashedPan(String hashedPan) {
        this.hashedPan = hashedPan;
    }

    @Override
    public String toString() {
        return "Trips{" +
                "started=" + started +
                ", finished=" + finished +
                ", durationSec=" + durationSec +
                ", fromStopId='" + fromStopId + '\'' +
                ", toStopId='" + toStopId + '\'' +
                ", chargeAmount=" + chargeAmount +
                ", companyId='" + companyId + '\'' +
                ", busId='" + busId + '\'' +
                ", hashedPan='" + hashedPan + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
