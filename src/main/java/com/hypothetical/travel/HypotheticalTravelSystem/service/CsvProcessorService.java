package com.hypothetical.travel.HypotheticalTravelSystem.service;

import com.hypothetical.travel.HypotheticalTravelSystem.model.TouchData;
import com.hypothetical.travel.HypotheticalTravelSystem.utils.TrimSpaceMappingStrategy;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class CsvProcessorService {

    private final ResourceLoader resourceLoader;

    public CsvProcessorService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private final Logger logger = LoggerFactory.getLogger(TripProcessor.class);

    public List<TouchData> readFromCsv(String fileName) {
        logger.info("Reading and parsing filename: {}", fileName);
        Resource resource = resourceLoader.getResource("classpath:" + fileName);
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            reader.readNext();// remove header
            CsvToBean<TouchData> csvToBean = new CsvToBeanBuilder<TouchData>(reader)
                    .withType(TouchData.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withMappingStrategy(new TrimSpaceMappingStrategy<>(TouchData.class))
                    .withSeparator(',')
                    .build();
            return csvToBean.parse();
        } catch (Exception ex) {
            logger.error("Error while reading and parsing filename: {}", ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public void writeToCsv(String[] header, List<String[]> data, String filPath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filPath))) {
            writer.writeNext(header);
            writer.writeAll(data);
        }
    }
}

