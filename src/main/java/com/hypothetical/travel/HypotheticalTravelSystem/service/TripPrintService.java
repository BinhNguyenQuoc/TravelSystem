package com.hypothetical.travel.HypotheticalTravelSystem.service;

import com.hypothetical.travel.HypotheticalTravelSystem.model.Summary;
import com.hypothetical.travel.HypotheticalTravelSystem.model.TripStatus;
import com.hypothetical.travel.HypotheticalTravelSystem.model.Trips;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class TripPrintService {


    @Value("${output.folder}")
    private String folder;


    @Autowired
    CsvProcessorService fileService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public void printCompleteTrip(List<Trips> trips) throws IOException {
        String[] header = {"started", "finished", "DurationSec", "fromStopId", "toStopId",
                "ChargeAmount", "CompanyId", "BusId", "HashedPan", "Status"};

        List<Trips> data = trips.stream()
                .filter(c -> c.getStatus().equals(TripStatus.COMPLETED.getValue())
                        || c.getStatus().equals(TripStatus.INCOMPLETED.getValue()))
                .toList();

        List<String[]> list = data.stream()
                .map(export -> {
                    String started = (export.getStarted() == null) ? "" : export.getStarted().format(formatter);
                    String finish = (export.getFinished() == null) ? "" : export.getFinished().format(formatter);
                    return new String[]{
                            started,
                            finish,
                            String.valueOf(export.getDurationSec()),
                            export.getFromStopId(),
                            export.getToStopId(),
                            String.valueOf(export.getChargeAmount()),
                            export.getCompanyId(), export.getBusId(), export.getHashedPan(), export.getStatus()};
                })
                .toList();
        fileService.writeToCsv(header, list, folder + "/trips.csv");
    }

    public void printUnproceseedTrip(List<Trips> trips) throws IOException {
        String[] header = {"started", "finished", "DurationSec", "fromStopId", "toStopId",
                "ChargeAmount", "CompanyId", "BusId", "HashedPan", "Status"};
        List<Trips> data = trips.stream()
                .filter(c -> c.getStatus().equals(TripStatus.UNPROCESSED_NO_PAN.getValue())
                        || c.getStatus().equals(TripStatus.WRONG_DATE_TIME.getValue())
                        || c.getStatus().equals(TripStatus.MISSING_DATA.getValue())
                        || c.getStatus().equals(TripStatus.CANCELLED.getValue()))
                .toList();

        List<String[]> list = data.stream()
                .map(export ->{
                    String started = (export.getStarted() == null) ? "" : export.getStarted().format(formatter);
                    String finish = (export.getFinished() == null) ? "" : export.getFinished().format(formatter);
                    return new String[]{
                            started,
                            finish,
                        String.valueOf(export.getDurationSec()),
                        export.getFromStopId(),
                        export.getToStopId(),
                        String.valueOf(export.getChargeAmount()),
                        export.getCompanyId(), export.getBusId(), export.getHashedPan(), export.getStatus()};
                })
                .toList();
        fileService.writeToCsv(header, list, folder + "/unprocessableTouchData.csv");
    }

    public void printSummaryTrip(List<Trips> completedList, List<Trips> unprocessedList) throws IOException {
        String[] header = {"date", "CompanyId", "BusId", "CompleteTripCount", "IncompleteTripCount",
                "CancelledTripCount", "TotalCharges"};

        List<Summary> list = new ArrayList<>();

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


        for (Map.Entry<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> date : map1.entrySet()) {
            Map<String, Map<String, ConcurrentMap<Double, Long>>> value = date.getValue();
            for (Map.Entry<String, Map<String, ConcurrentMap<Double, Long>>> company : value.entrySet()) {
                Map<String, ConcurrentMap<Double, Long>> bus = company.getValue();
                for (Map.Entry<String, ConcurrentMap<Double, Long>> charge : bus.entrySet()) {
                    double totalCharge = 0.0;
                    for (ConcurrentMap.Entry<Double, Long> count : charge.getValue().entrySet()) {
                        totalCharge += count.getKey();
                    }
                    list.add(new Summary(
                            date.getKey(),
                            company.getKey(),
                            charge.getKey(),
                            charge.getValue().entrySet().size(),
                            0,
                            0,
                            totalCharge));
                }

            }
        }

        for (Map.Entry<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> date : map2.entrySet()) {
            Map<String, Map<String, ConcurrentMap<Double, Long>>> value = date.getValue();
            for (Map.Entry<String, Map<String, ConcurrentMap<Double, Long>>> company : value.entrySet()) {
                Map<String, ConcurrentMap<Double, Long>> bus = company.getValue();
                for (Map.Entry<String, ConcurrentMap<Double, Long>> charge : bus.entrySet()) {
                    double totalCharge = 0.0;
                    for (ConcurrentMap.Entry<Double, Long> count : charge.getValue().entrySet()) {
                        totalCharge += count.getKey();
                    }
                    list.add(new Summary(
                            date.getKey(),
                            company.getKey(),
                            charge.getKey(),
                            0,
                            charge.getValue().entrySet().size(),
                            0,
                            totalCharge));

                }

            }
        }

        for (Map.Entry<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> date : map3.entrySet()) {
            Map<String, Map<String, ConcurrentMap<Double, Long>>> value = date.getValue();
            for (Map.Entry<String, Map<String, ConcurrentMap<Double, Long>>> company : value.entrySet()) {
                Map<String, ConcurrentMap<Double, Long>> bus = company.getValue();
                for (Map.Entry<String, ConcurrentMap<Double, Long>> charge : bus.entrySet()) {
                    double totalCharge = 0.0;
                    for (ConcurrentMap.Entry<Double, Long> count : charge.getValue().entrySet()) {
                        totalCharge += count.getKey();
                    }
                    list.add(new Summary(
                            date.getKey(),
                            company.getKey(),
                            charge.getKey(),
                            0,
                            0,
                            charge.getValue().entrySet().size(),
                            totalCharge));
                }

            }
        }

        List<Summary> sortedList = list.stream()
                .sorted(Comparator.comparing(Summary::getDate)
                        .thenComparing(Summary::getCompanyId)
                        .thenComparing(Summary::getBusId))
                .toList();

        List<String[]> outputList = new ArrayList<>();

        sortedList.forEach(
                data -> {
                    outputList.add(new String[]{
                            String.valueOf(data.getDate()),
                            data.getCompanyId(),
                            data.getBusId(),
                            String.valueOf(data.getCompleteTripCount()),
                            String.valueOf(data.getIncompleteTripCount()),
                            String.valueOf(data.getCancelledTripCount()),
                            String.valueOf(data.getTotalCharges())});

                }
        );

        fileService.writeToCsv(header, outputList, folder + "/summary.csv");
    }


}
