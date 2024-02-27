package com.hypothetical.travel.HypotheticalTravelSystem.model;

import com.hypothetical.travel.HypotheticalTravelSystem.utils.LocalDateTimeConverter;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Trips {

    @CsvCustomBindByPosition(position = 0, converter = LocalDateTimeConverter.class)
    private LocalDateTime started;

    @CsvCustomBindByPosition(position = 1, converter = LocalDateTimeConverter.class)
    private LocalDateTime finished;

    @CsvBindByPosition(position = 2)
    private long durationSec;

    @CsvBindByPosition(position = 3)
    private String fromStopId;

    @CsvBindByPosition(position = 4)
    private String toStopId;

    @CsvBindByPosition(position = 5)
    private double chargeAmount;

    @CsvBindByPosition(position = 6)
    private String companyId;

    @CsvBindByPosition(position = 7)
    private String busId;

    @CsvBindByPosition(position = 8)
    private String hashedPan;

    @CsvBindByPosition(position = 9)
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
