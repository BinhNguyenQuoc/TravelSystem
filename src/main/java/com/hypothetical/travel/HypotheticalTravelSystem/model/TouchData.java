package com.hypothetical.travel.HypotheticalTravelSystem.model;

import com.hypothetical.travel.HypotheticalTravelSystem.utils.LocalDateTimeConverter;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TouchData {
    @CsvBindByPosition(position = 0)
    private String id;

    @CsvCustomBindByPosition(position = 1, converter = LocalDateTimeConverter.class)
    private LocalDateTime localDateTime;

    @CsvBindByPosition(position = 2)
    private String touchType;

    @CsvBindByPosition(position = 3)
    private String stopID;

    @CsvBindByPosition(position = 4)
    private String companyId;

    @CsvBindByPosition(position = 5)
    private String busId;

    @CsvBindByPosition(position = 6)
    private String pan;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getTouchType() {
        return touchType;
    }

    public void setTouchType(String touchType) {
        this.touchType = touchType;
    }

    public String getStopID() {
        return stopID;
    }

    public void setStopID(String stopID) {
        this.stopID = stopID;
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

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    @Override
    public String toString() {
        return "TouchData{" +
                "id='" + id + '\'' +
                ", localDateTime=" + localDateTime +
                ", touchType='" + touchType + '\'' +
                ", stopID='" + stopID + '\'' +
                ", companyId='" + companyId + '\'' +
                ", busId='" + busId + '\'' +
                ", pan='" + pan + '\'' +
                '}';
    }
}
