package io.github.jlmc.uploadcsv.csv.boundary;

import com.opencsv.ICSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;
import io.github.jlmc.uploadcsv.csv.entity.CsvReaderResult;
import io.github.jlmc.uploadcsv.csv.entity.Violation;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractCsvReader<E, R> implements CsvReader<E> {

    private final Supplier<HeaderColumnNameMappingStrategy<R>> columnNameMappingStrategy;

    protected AbstractCsvReader(Supplier<HeaderColumnNameMappingStrategy<R>> columnNameMappingStrategy) {
        this.columnNameMappingStrategy = columnNameMappingStrategy;
    }

    @Override
    public CsvReaderResult<E> read(String accountId, String csvContent) {
        StringReader reader = new StringReader(csvContent);

        CsvToBean<R> csvCsvToBean = createStatefulBeanToCsv(reader);

        List<R> items = parse(csvCsvToBean);

        List<CsvException> capturedExceptions = csvCsvToBean.getCapturedExceptions();

        List<Violation> violations =
                capturedExceptions.stream()
                                  .map(it -> new Violation(it.getLineNumber(), it.getMessage(), it))
                                  .collect(Collectors.toList());

        List<E> entities = toEntities(accountId, items);

        return new CsvReaderResult<>(entities, violations);
    }

    protected abstract List<E> toEntities(String accountId, List<R> resources);

    private CsvToBean<R> createStatefulBeanToCsv(Reader reader) {
        HeaderColumnNameMappingStrategy<R> rHeaderColumnNameMappingStrategy = columnNameMappingStrategy.get();
        return new CsvToBeanBuilder<R>(reader)
                .withMappingStrategy(rHeaderColumnNameMappingStrategy)
                .withQuoteChar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withOrderedResults(true)
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withThrowExceptions(false)
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                .build();
    }

    private List<R> parse(CsvToBean<R> csvCsvToBean) {
        try {
            return csvCsvToBean.parse();
        } catch (Exception e) {
            throw handledException(e);
        }
    }

    private CsvIllegalDataException handledException(Exception exception) {
        if (exception.getCause() instanceof CsvException csvException) {
            var lineNumber = csvException.getLineNumber();
            var errorName = (lineNumber < 0) ? "Csv Header" : "Line " + lineNumber;
            var errorDetail = csvException.getMessage();
            return new CsvIllegalDataException(errorName, errorDetail, exception.getMessage(), csvException);
        } else {
            return new CsvIllegalDataException(null, null, exception.getMessage(), exception);
        }
    }
}
