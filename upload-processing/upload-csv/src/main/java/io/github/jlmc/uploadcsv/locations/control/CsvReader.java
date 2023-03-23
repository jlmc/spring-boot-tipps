package io.github.jlmc.uploadcsv.locations.control;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import io.github.jlmc.uploadcsv.locations.entity.Location;

import java.io.Reader;
import java.util.List;

import static com.opencsv.ICSVWriter.DEFAULT_SEPARATOR;

public class CsvReader {

    private static CsvToBean<Location> createCsvToBeans(Reader reader) {
        return new CsvToBeanBuilder<Location>(reader)
                .withMappingStrategy(CsvConfigurations.getLocationHeaderColumnNameMappingStrategy())
                //.withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .withQuoteChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withOrderedResults(true)
                .withSeparator(DEFAULT_SEPARATOR)

                .withThrowExceptions(false)

                .build();
    }

    public CsvReadResult<Location> read(Reader reader) {
        CsvToBean<Location> csvToBean = createCsvToBeans(reader);

        List<Location> items = csvToBean.parse();
        List<CsvLineViolation> violations =
                csvToBean.getCapturedExceptions()
                         .stream().map(ex -> new CsvLineViolation(ex.getLineNumber(), ex.getMessage())).toList();

        return new CsvReadResult<>(items, violations);
    }


}
