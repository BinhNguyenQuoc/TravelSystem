package com.hypothetical.travel.HypotheticalTravelSystem.service;

import com.hypothetical.travel.HypotheticalTravelSystem.model.TouchData;
import com.hypothetical.travel.HypotheticalTravelSystem.utils.TrimSpaceMappingStrategy;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
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

    public List<TouchData> readFromCsv(String fileName) {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToCsv(String[] header, List<String[]> data, String filPath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filPath))) {
            writer.writeNext(header);
            writer.writeAll(data);
        }
    }
}

