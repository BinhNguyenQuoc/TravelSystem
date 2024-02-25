package com.hypothetical.travel.HypotheticalTravelSystem.utils;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvFieldAssignmentException;

import java.util.Arrays;

public class TrimSpaceMappingStrategy <T> extends ColumnPositionMappingStrategy<T> {

    public TrimSpaceMappingStrategy(Class<T> type) {
        setType(type); // Set the type of the bean class
    }
    @Override
    public T populateNewBean(String[] line) throws CsvChainedException, CsvFieldAssignmentException {
        String[] trimmedLine = Arrays.stream(line)
                .map(String::trim)
                .toArray(String[]::new);
        return super.populateNewBean(trimmedLine);
    }

    // Implement other methods of MappingStrategy interface as needed
}
