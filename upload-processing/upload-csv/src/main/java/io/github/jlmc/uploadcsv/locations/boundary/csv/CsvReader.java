package io.github.jlmc.uploadcsv.locations.boundary.csv;

public interface CsvReader<T> {

    CsvReaderResult<T> read(String accountId, String csvContent);
}
