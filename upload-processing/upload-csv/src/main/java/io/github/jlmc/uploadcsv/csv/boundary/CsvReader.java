package io.github.jlmc.uploadcsv.csv.boundary;

import io.github.jlmc.uploadcsv.csv.entity.CsvReaderResult;

public interface CsvReader<T> {

    CsvReaderResult<T> read(String accountId, String csvContent);
}
