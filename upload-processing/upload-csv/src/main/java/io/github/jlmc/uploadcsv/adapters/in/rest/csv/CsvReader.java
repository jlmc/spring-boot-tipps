package io.github.jlmc.uploadcsv.adapters.in.rest.csv;

public interface CsvReader<T> {

    CsvReaderResult<T> read(String accountId, String csvContent);
}
