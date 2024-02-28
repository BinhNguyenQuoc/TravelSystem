package com.hypothetical.travel.HypotheticalTravelSystem.service;

import com.hypothetical.travel.HypotheticalTravelSystem.model.Summary;
import com.hypothetical.travel.HypotheticalTravelSystem.model.TripStatus;
import com.hypothetical.travel.HypotheticalTravelSystem.model.Trips;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class TripPrintService {


    @Value("${output.folder}")
    private String folder;


    @Autowired
    CsvProcessorService fileService;

    private final Logger logger = LoggerFactory.getLogger(TripProcessor.class);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public void printCompleteTrip(List<Trips> trips) throws IOException {
        logger.info("Printing the completed trips");
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
        logger.info("Complete to print the completed trips");
    }

    public void printUnprocessedTrip(List<Trips> trips) throws IOException {
        logger.info("Printing the unprocessed trips");
        String[] header = {"started", "finished", "DurationSec", "fromStopId", "toStopId",
                "ChargeAmount", "CompanyId", "BusId", "HashedPan", "Status"};
        List<Trips> data = trips.stream()
                .filter(c -> c.getStatus().equals(TripStatus.UNPROCESSED_NO_PAN.getValue())
                        || c.getStatus().equals(TripStatus.WRONG_DATE_TIME.getValue())
                        || c.getStatus().equals(TripStatus.MISSING_DATA.getValue())
                        || c.getStatus().equals(TripStatus.CANCELLED.getValue()))
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
        fileService.writeToCsv(header, list, folder + "/unprocessableTouchData.csv");
        logger.info("Complete to print the unprocessed trips");
    }

    public void printSummaryTrip(List<Trips> completedList, List<Trips> unprocessedList) throws IOException {
        logger.info("Printing the summary trips");
        String[] header = {"date", "CompanyId", "BusId", "CompleteTripCount", "IncompleteTripCount",
                "CancelledTripCount", "TotalCharges"};

        List<Summary> list = new ArrayList<>();

        Map<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> map1 = completedList.stream()
                .filter(c -> c.getStatus().equals(TripStatus.COMPLETED.getValue()))
                .collect(Collectors.groupingBy(Trips::getDateOnly,
                        Collectors.groupingBy(Trips::getCompanyId,
                                Collectors.groupingBy(Trips::getBusIdAndStopAndTime,
                                        Collectors.groupingByConcurrent(Trips::getChargeAmount, Collectors.counting())))));

        Map<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> map2 = completedList.stream()
                .filter(c -> c.getStatus().equals(TripStatus.INCOMPLETED.getValue()))
                .collect(Collectors.groupingBy(Trips::getDateOnly,
                        Collectors.groupingBy(Trips::getCompanyId,
                                Collectors.groupingBy(Trips::getBusIdAndStopAndTime,
                                        Collectors.groupingByConcurrent(Trips::getChargeAmount, Collectors.counting())))));

        Map<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> map3 = unprocessedList.stream()
                .filter(c -> c.getStatus().equals(TripStatus.CANCELLED.getValue()))
                .collect(Collectors.groupingBy(Trips::getDateOnly,
                        Collectors.groupingBy(Trips::getCompanyId,
                                Collectors.groupingBy(Trips::getBusIdAndStopAndTime,
                                        Collectors.groupingByConcurrent(Trips::getChargeAmount, Collectors.counting())))));


        buildData(map1, list, 0);
        buildData(map2, list, 1);
        buildData(map3, list, 2);

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
        logger.info("Complete to print the summary trips");
    }

    private static void buildData(Map<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> map1, List<Summary> list, int index) {
        for (Map.Entry<LocalDate, Map<String, Map<String, ConcurrentMap<Double, Long>>>> date : map1.entrySet()) {
            for (Map.Entry<String, Map<String, ConcurrentMap<Double, Long>>> bus : date.getValue().entrySet()) {
                Map<String, Double> mapCharge = new HashMap<>();
                Map<String, Integer> mapCount = new HashMap<>();
                int i = 0;
                for (Map.Entry<String, ConcurrentMap<Double, Long>> busCharge : bus.getValue().entrySet()) {
                    i = 0;
                    String busId = busCharge.getKey().split("_")[0];
                    if (mapCount.get(busId) == null) {
                        mapCount.put(busId, ++i);
                    } else {
                        mapCount.put(busId, mapCount.get(busId) + 1);
                    }
                    for (ConcurrentMap.Entry<Double, Long> count : busCharge.getValue().entrySet()) {
                        if (mapCharge.get(busId) == null) {
                            mapCharge.put(busId, count.getKey());
                        } else {
                            double sum = mapCharge.get(busId) + count.getKey();
                            mapCharge.put(busId, sum);
                        }
                    }
                }
                for (Map.Entry<String, Double> data : mapCharge.entrySet()) {
                    if (index == 0) {
                        list.add(new Summary(
                                date.getKey(),
                                bus.getKey(),
                                data.getKey(),
                                mapCount.get(data.getKey()),
                                0,
                                0,
                                data.getValue()));
                    } else if (index == 1) {
                        list.add(new Summary(
                                date.getKey(),
                                bus.getKey(),
                                data.getKey(),
                                0,
                                mapCount.get(data.getKey()),
                                0,
                                data.getValue()));
                    } else if (index == 2) {
                        list.add(new Summary(
                                date.getKey(),
                                bus.getKey(),
                                data.getKey(),
                                0,
                                0,
                                mapCount.get(data.getKey()),
                                data.getValue()));

                    }
                }
            }
        }
    }


}
