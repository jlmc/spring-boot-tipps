package io.github.jlmc.uploadcsv.locations.control;

import com.opencsv.CSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.springframework.stereotype.Component;

import java.io.Writer;
import java.util.List;


@Component
public class CsvWriter {

    private static StatefulBeanToCsv<Location> createBeanToCsv(Writer writer) {
        HeaderColumnNameMappingStrategy<Location> headerColumnNameMappingStrategy = CsvConfigurations.getLocationHeaderColumnNameMappingStrategy();

        return new StatefulBeanToCsvBuilder<Location>(writer)
                .withQuotechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                //.withLineEnd(RFC4180_LINE_END)
                .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withMappingStrategy(headerColumnNameMappingStrategy)
                .withOrderedResults(true)
                .withThrowExceptions(false)
                .build();
    }

    public Writer write(List<Location> locations, Writer writer) {
        try {
            StatefulBeanToCsv<Location> beanToCsv = createBeanToCsv(writer);

            beanToCsv.write(locations);
            return writer;
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IllegalStateException(e);
        }
    }
}
