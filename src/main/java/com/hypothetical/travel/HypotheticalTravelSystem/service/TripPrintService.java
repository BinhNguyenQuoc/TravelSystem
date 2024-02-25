package com.hypothetical.travel.HypotheticalTravelSystem.service;

import com.hypothetical.travel.HypotheticalTravelSystem.model.TripStatus;
import com.hypothetical.travel.HypotheticalTravelSystem.model.Trips;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.summingDouble;

@Service
public class TripPrintService {

    @Autowired
    CsvProcessorService fileService;

    public void printTrips(List<Trips> trips) throws IOException {
        List<Trips> completedList = printCompleteTrip(trips);
        List<Trips> unprocessedList = printUnproceseedTrip(trips);
        printSummaryTrip(completedList, unprocessedList);
    }

    private List<Trips> printCompleteTrip(List<Trips> trips) throws IOException {
        String[] header = {"started", "finished", "DurationSec", "fromStopId", "toStopId",
                "ChargeAmount", "CompanyId", "BusId", "HashedPan", "Status"};
        List<Trips> data = trips.stream()
                .filter(c -> c.getStatus().equals(TripStatus.COMPLETED.getValue())
                        || c.getStatus().equals(TripStatus.INCOMPLETED.getValue()))
                .toList();

        List<String[]> list = data.stream()
                .map(export -> new String[]{String.valueOf(export.getStarted()),
                        String.valueOf(export.getFinished()),
                        String.valueOf(export.getDurationSec()),
                        export.getFromStopId(),
                        export.getToStopId(),
                        String.valueOf(export.getChargeAmount()),
                        export.getCompanyId(), export.getBusId(), export.getHashedPan(), export.getStatus()})
                .toList();
        fileService.writeToCsv(header, list, "output/trips.csv");
        return data;
    }

    private List<Trips> printUnproceseedTrip(List<Trips> trips) throws IOException {
        String[] header = {"started", "finished", "DurationSec", "fromStopId", "toStopId",
                "ChargeAmount", "CompanyId", "BusId", "HashedPan", "Status"};
        List<Trips> data = trips.stream()
                .filter(c -> c.getStatus().equals(TripStatus.UNPROCESSED_NO_PAN.getValue())
                        || c.getStatus().equals(TripStatus.WRONG_DATE_TIME.getValue())
                        || c.getStatus().equals(TripStatus.MISSING_DATA.getValue())
                        || c.getStatus().equals(TripStatus.CANCELLED.getValue()))
                .toList();

        List<String[]> list = data.stream()
                .map(export -> new String[]{String.valueOf(export.getStarted()),
                        String.valueOf(export.getFinished()),
                        String.valueOf(export.getDurationSec()),
                        export.getFromStopId(),
                        export.getToStopId(),
                        String.valueOf(export.getChargeAmount()),
                        export.getCompanyId(), export.getBusId(), export.getHashedPan(), export.getStatus()})
                .toList();
        fileService.writeToCsv(header, list, "output/unprocessableTouchData.csv");
        return data;
    }

    private void printSummaryTrip(List<Trips> completedList, List<Trips> unprocessedList) throws IOException {
        String[] header = {"date", "CompanyId", "BusId", "CompleteTripCount", "IncompleteTripCount",
                "CancelledTripCount", "TotalCharges"};

        Map<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> map1 = completedList.stream()
                .filter(c -> c.getStatus().equals(TripStatus.COMPLETED.getValue()))
                .collect(Collectors.groupingBy(Trips::getDateOnly,
                        Collectors.groupingBy(Trips::getCompanyId,
                                Collectors.groupingBy(Trips::getBusId,
                                Collectors.groupingByConcurrent(Trips::getChargeAmount, Collectors.counting())))));

        Map<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> map2 = completedList.stream()
                .filter(c -> c.getStatus().equals(TripStatus.INCOMPLETED.getValue()))
                .collect(Collectors.groupingBy(Trips::getDateOnly,
                        Collectors.groupingBy(Trips::getCompanyId,
                                Collectors.groupingBy(Trips::getBusId,
                                        Collectors.groupingByConcurrent(Trips::getChargeAmount, Collectors.counting())))));

        Map<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> map3 = unprocessedList.stream()
                .filter(c -> c.getStatus().equals(TripStatus.CANCELLED.getValue()))
                .collect(Collectors.groupingBy(Trips::getDateOnly,
                        Collectors.groupingBy(Trips::getCompanyId,
                                Collectors.groupingBy(Trips::getBusId,
                                        Collectors.groupingByConcurrent(Trips::getChargeAmount, Collectors.counting())))));


        List<String[]> list = new ArrayList<>();

        for (Map.Entry<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> date: map1.entrySet()) {
            Map<String, Map<String, ConcurrentMap<Double, Long>>> value = date.getValue();
            for (Map.Entry<String, Map<String, ConcurrentMap<Double, Long>>> company: value.entrySet()) {
                Map<String, ConcurrentMap<Double, Long>> bus = company.getValue();
                for (Map.Entry<String, ConcurrentMap<Double, Long>> charge: bus.entrySet()) {
                    double totalCharge = 0.0;
                    for (ConcurrentMap.Entry<Double, Long> count: charge.getValue().entrySet()) {
                        totalCharge+= count.getKey();
                    }
                    list.add(new String[]{
                            String.valueOf(date.getKey()),
                            company.getKey(),
                            charge.getKey(),
                                    String.valueOf(charge.getValue().entrySet().size()),
                            String.valueOf(0),
                            String.valueOf(0),
                            String.valueOf(totalCharge)});
                }

            }
        }

        for (Map.Entry<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> date: map2.entrySet()) {
            Map<String, Map<String, ConcurrentMap<Double, Long>>> value = date.getValue();
            for (Map.Entry<String, Map<String, ConcurrentMap<Double, Long>>> company: value.entrySet()) {
                Map<String, ConcurrentMap<Double, Long>> bus = company.getValue();
                for (Map.Entry<String, ConcurrentMap<Double, Long>> charge: bus.entrySet()) {
                    double totalCharge = 0.0;
                    for (ConcurrentMap.Entry<Double, Long> count: charge.getValue().entrySet()) {
                        totalCharge+= count.getKey();
                    }
                    list.add(new String[]{
                            String.valueOf(date.getKey()),
                            company.getKey(),
                            charge.getKey(),
                            String.valueOf(0),
                            String.valueOf(charge.getValue().entrySet().size()),
                            String.valueOf(0),
                            String.valueOf(totalCharge)});
                }

            }
        }

        for (Map.Entry<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> date: map3.entrySet()) {
            Map<String, Map<String, ConcurrentMap<Double, Long>>> value = date.getValue();
            for (Map.Entry<String, Map<String, ConcurrentMap<Double, Long>>> company: value.entrySet()) {
                Map<String, ConcurrentMap<Double, Long>> bus = company.getValue();
                for (Map.Entry<String, ConcurrentMap<Double, Long>> charge: bus.entrySet()) {
                    double totalCharge = 0.0;
                    for (ConcurrentMap.Entry<Double, Long> count: charge.getValue().entrySet()) {
                        totalCharge+= count.getKey();
                    }
                    list.add(new String[]{
                            String.valueOf(date.getKey()),
                            company.getKey(),
                            charge.getKey(),
                            String.valueOf(0),
                            String.valueOf(0),
                            String.valueOf(charge.getValue().entrySet().size()),
                            String.valueOf(totalCharge)});
                }

            }
        }

        System.out.println(map1);
        fileService.writeToCsv(header, list, "output/summary.csv");
    }


}
