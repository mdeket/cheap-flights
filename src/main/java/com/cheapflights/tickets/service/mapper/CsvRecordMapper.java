package com.cheapflights.tickets.service.mapper;

import org.apache.commons.csv.CSVRecord;

public interface CsvRecordMapper<T> {
    T fromCsvRecord(CSVRecord csvRecord);
}
