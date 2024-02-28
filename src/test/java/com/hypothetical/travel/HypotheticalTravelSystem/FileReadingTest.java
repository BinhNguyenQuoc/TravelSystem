package com.hypothetical.travel.HypotheticalTravelSystem;

import com.hypothetical.travel.HypotheticalTravelSystem.model.Summary;
import com.hypothetical.travel.HypotheticalTravelSystem.model.TouchData;
import com.hypothetical.travel.HypotheticalTravelSystem.model.TripStatus;
import com.hypothetical.travel.HypotheticalTravelSystem.model.Trips;
import com.hypothetical.travel.HypotheticalTravelSystem.service.CsvProcessorService;
import com.hypothetical.travel.HypotheticalTravelSystem.service.TripPrintService;
import com.hypothetical.travel.HypotheticalTravelSystem.service.TripProcessor;
import com.hypothetical.travel.HypotheticalTravelSystem.utils.TrimSpaceMappingStrategy;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileReadingTest {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    CsvProcessorService fileService;

    @Autowired
    TripProcessor tripProcessor;

    @Autowired
    TripPrintService tripPrintService;

    @Value("${output.folder}")
    private String folderPath;


    @BeforeAll()
    public void setup() {
        Path folder = Paths.get(folderPath);

        if (!Files.exists(folder)) {
            try {
                Files.createDirectories(folder);
                System.out.println("Folder created successfully.");
            } catch (IOException e) {
                System.err.println("Failed to create folder: " + e.getMessage());
            }
        }
    }

    @AfterAll
    public void delete() throws InterruptedException {

        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            // Get the list of files and subdirectories in the directory
            File[] files = folder.listFiles();
            if (files != null) {
                // Delete each file and subdirectory recursively
                for (File file : files) {
                    if (!file.isDirectory()) {
                        file.delete();
                    }
                }
            }
        }

        // Delete the folder itself after its contents have been deleted
        folder.delete();
        System.out.println("Folder " + folder + " created successfully.");
    }

    @Test
    public void testTrip() throws IOException, InterruptedException {

        List<TouchData> touchDataTest = fileService.readFromCsv("touchDataTest.csv");
        List<Trips> trips = tripProcessor.getCompletedTrips(touchDataTest);
        tripPrintService.printCompleteTrip(trips);

        assertNotNull(trips);
        assertEquals(trips.size(), 7);
        validateTrips(trips);
    }

    @Test
    public void testUnProcessedTrip() throws IOException, InterruptedException {
        List<TouchData> touchDataTest = fileService.readFromCsv("touchDataTest.csv");
        List<Trips> trips = tripProcessor.getUnprocessedTrips(touchDataTest);
        tripPrintService.printUnprocessedTrip(trips);

        assertNotNull(trips);
        assertEquals(trips.size(), 4);
        validateUnprocessed(trips);
    }

    @Test
    public void testSummaryTrip() throws IOException, InterruptedException {
        List<TouchData> touchDataTest = fileService.readFromCsv("touchDataTest.csv");
        List<Trips> completedTrips = tripProcessor.getCompletedTrips(touchDataTest);
        List<Trips> unprocessedTrips = tripProcessor.getUnprocessedTrips(touchDataTest);
        tripPrintService.printSummaryTrip(completedTrips, unprocessedTrips);

        validateSummary(completedTrips, unprocessedTrips);
    }

    private void validateTrips(List<Trips> trips) {
        List<Trips> data = readTripsFromCsv(folderPath + "/trips.csv");
        assert data != null;
        assertEquals(trips.size(), data.size());

        for (int i = 0; i < data.size(); i++) {
            Trips tripInput = trips.get(i);
            Trips expectedOutput = data.get(i);
            assertEquals(tripInput.getStarted(), expectedOutput.getStarted());
            assertEquals(tripInput.getFinished(), expectedOutput.getFinished());
            assertEquals(tripInput.getChargeAmount(), expectedOutput.getChargeAmount());
            assertEquals(tripInput.getStatus(), expectedOutput.getStatus());
        }
    }

    private void validateUnprocessed(List<Trips> trips) {
        List<Trips> data = readTripsFromCsv(folderPath + "/unprocessableTouchData.csv");
        assert data != null;
        assertEquals(trips.size(), data.size());

        for (int i = 0; i < data.size(); i++) {
            Trips tripInput = trips.get(i);
            Trips expectedOutput = data.get(i);
            assertEquals(tripInput.getStarted(), expectedOutput.getStarted());
            assertEquals(tripInput.getFinished(), expectedOutput.getFinished());
            assertEquals(tripInput.getChargeAmount(), expectedOutput.getChargeAmount());
            assertEquals(tripInput.getStatus(), expectedOutput.getStatus());
        }
    }

    private void validateSummary(List<Trips> completedTrips, List<Trips> unprocessedTrips) {
        List<Summary> data = readSummaryFromCsv(folderPath + "/summary.csv");
        assert data != null;

        List<Trips> trips = Stream.concat(completedTrips.stream(), unprocessedTrips.stream()).toList();

        long totalCompletedTripCountExpect = completedTrips.stream().filter(c -> c.getStatus().equals(TripStatus.COMPLETED.getValue())).count();
        int completedTripCountResult = data.stream().map(Summary::getCompleteTripCount).mapToInt(Integer::intValue).sum();
        assertEquals(totalCompletedTripCountExpect, completedTripCountResult);

        long totalCancelledTripCountExpect = unprocessedTrips.stream().filter(c -> c.getStatus().equals(TripStatus.CANCELLED.getValue())).count();
        int cancelledTripCountResult = data.stream().map(Summary::getCancelledTripCount).mapToInt(Integer::intValue).sum();
        assertEquals(totalCancelledTripCountExpect, cancelledTripCountResult);

        long totalIncompletedTripCountExpect = completedTrips.stream().filter(c -> c.getStatus().equals(TripStatus.INCOMPLETED.getValue())).count();
        int incompletedTripCountResult = data.stream().map(Summary::getIncompleteTripCount).mapToInt(Integer::intValue).sum();
        assertEquals(totalIncompletedTripCountExpect, incompletedTripCountResult);

        // TODO: validate more
    }

    private List<Trips> readTripsFromCsv(String filePath) {
        try (CSVReader reader = new CSVReader(new BufferedReader(new FileReader(filePath)))) {
            reader.readNext();
            CsvToBean<Trips> csvToBean = new CsvToBeanBuilder<Trips>(reader)
                    .withType(Trips.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withMappingStrategy(new TrimSpaceMappingStrategy<>(Trips.class))
                    .withSeparator(',')
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private List<Summary> readSummaryFromCsv(String filePath) {
        try (CSVReader reader = new CSVReader(new BufferedReader(new FileReader(filePath)))) {
            reader.readNext();
            CsvToBean<Summary> csvToBean = new CsvToBeanBuilder<Summary>(reader)
                    .withType(Summary.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withMappingStrategy(new TrimSpaceMappingStrategy<>(Summary.class))
                    .withSeparator(',')
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
