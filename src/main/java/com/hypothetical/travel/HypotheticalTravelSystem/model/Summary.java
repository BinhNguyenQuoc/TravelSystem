package com.hypothetical.travel.HypotheticalTravelSystem.model;

import com.hypothetical.travel.HypotheticalTravelSystem.utils.LocalDateConverter;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;

import java.time.LocalDate;


public class Summary {
    @CsvCustomBindByPosition(position = 0, converter = LocalDateConverter.class)
    private LocalDate date;

    @CsvBindByPosition(position = 1)
    private String companyId;

    @CsvBindByPosition(position = 2)
    private String busId;

    @CsvBindByPosition(position = 3)
    private int completeTripCount;

    @CsvBindByPosition(position = 4)
    private int incompleteTripCount;

    @CsvBindByPosition(position = 5)
    private int cancelledTripCount;

    @CsvBindByPosition(position = 6)
    private double totalCharges;

    public Summary(){
    }

    public Summary(LocalDate date, String companyId, String busId, int completeTripCount, int incompleteTripCount, int cancelledTripCount, double totalCharges) {
        this.date = date;
        this.companyId = companyId;
        this.busId = busId;
        this.completeTripCount = completeTripCount;
        this.incompleteTripCount = incompleteTripCount;
        this.cancelledTripCount = cancelledTripCount;
        this.totalCharges = totalCharges;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public int getCompleteTripCount() {
        return completeTripCount;
    }

    public void setCompleteTripCount(int completeTripCount) {
        this.completeTripCount = completeTripCount;
    }

    public int getIncompleteTripCount() {
        return incompleteTripCount;
    }

    public void setIncompleteTripCount(int incompleteTripCount) {
        this.incompleteTripCount = incompleteTripCount;
    }

    public int getCancelledTripCount() {
        return cancelledTripCount;
    }

    public void setCancelledTripCount(int cancelledTripCount) {
        this.cancelledTripCount = cancelledTripCount;
    }

    public double getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(double totalCharges) {
        this.totalCharges = totalCharges;
    }
}
