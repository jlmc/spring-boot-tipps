package io.github.jlmc.uploadcsv.locations.boundary.csv.locations;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;
import io.github.jlmc.uploadcsv.locations.boundary.csv.CsvIllegalDataException;
import io.github.jlmc.uploadcsv.locations.boundary.csv.CsvReader;
import io.github.jlmc.uploadcsv.locations.boundary.csv.CsvReaderResult;
import io.github.jlmc.uploadcsv.locations.boundary.csv.Violation;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model.AddressCsvResource;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model.LocationCsvResource;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LocationsCsvReader implements CsvReader<Location> {

    @Override
    public CsvReaderResult<Location> read(String accountId, String csvContent) {
        StringReader reader = new StringReader(csvContent);


        CsvToBean<LocationCsvResource> csvCsvToBean = createStatefulBeanToCsv(reader);

        List<LocationCsvResource> items = parse(csvCsvToBean);

        List<CsvException> capturedExceptions = csvCsvToBean.getCapturedExceptions();

        List<Violation> violations =
                capturedExceptions.stream()
                                  .map(it -> new Violation(it.getLineNumber(), it.getMessage(), it))
                                  .collect(Collectors.toList());

        List<Location> locations =
                items.stream().map(it -> toEntity(accountId, it)).toList();


        return new CsvReaderResult<>(locations, violations);
    }

    private Location toEntity(String accountId, LocationCsvResource locationCsvResource) {
        return Location.builder()
                       .id(locationCsvResource.getId())
                       .accountId(accountId)
                       .name(locationCsvResource.getName())
                       .phoneNumber(locationCsvResource.getPhoneNumber())
                       .timeZone(locationCsvResource.getTimeZone())
                       .address(Optional.ofNullable(locationCsvResource.getAddress()).map(AddressCsvResource::toEntity).orElse(null))
                       .imageUrl(locationCsvResource.getImageUrl())
                       .openHours(locationCsvResource.getOpenHours())
                       .build();
    }


    private CsvToBean<LocationCsvResource> createStatefulBeanToCsv(Reader reader) {
        return new CsvToBeanBuilder<LocationCsvResource>(reader)
                .withMappingStrategy(LocationsColumnNameMappingStrategy.columnNameMappingStrategy())
                .withQuoteChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withOrderedResults(true)
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withThrowExceptions(false)
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                .build();
    }

    private List<LocationCsvResource> parse(CsvToBean<LocationCsvResource> csvCsvToBean) {
        try {
            return csvCsvToBean.parse();
        } catch (IllegalStateException e) {
            throw handledException(e);
        }
    }

    private CsvIllegalDataException handledException(Exception exception) {
        if (exception.getCause() instanceof CsvException csvException) {
            var lineNumber = csvException.getLineNumber();
            var errorName = (lineNumber < 0) ? "Csv Header" : "Line " + lineNumber;
            var errorDetail = csvException.getMessage();
            return new CsvIllegalDataException(errorName, errorDetail, csvException);
        } else {
            return new CsvIllegalDataException(null, null, exception);
        }
    }

}
