package com.cheapflights.tickets.service;

import org.apache.commons.csv.CSVRecord;

public interface CsvRecordMapper<T> {
    T fromCsvRecord(CSVRecord csvRecord);
}
