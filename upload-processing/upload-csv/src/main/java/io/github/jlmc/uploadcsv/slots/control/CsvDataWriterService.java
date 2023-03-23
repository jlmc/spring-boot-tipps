package io.github.jlmc.uploadcsv.slots.control;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import io.github.jlmc.uploadcsv.slots.entity.SpotLine;
import io.netty.buffer.ByteBufAllocator;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static com.opencsv.ICSVWriter.DEFAULT_SEPARATOR;
import static com.opencsv.ICSVWriter.RFC4180_LINE_END;

@AllArgsConstructor

@Service
public class CsvDataWriterService {

    private final SpotRepository spotRepository;

    public static Flux<DataBuffer> writeWorkDone(List<SpotLine> workDoneList) {

        try {
            StringWriter writer = new StringWriter();

            ColumnPositionMappingStrategy<SpotLine> mapStrategy = new ColumnPositionMappingStrategy<>();

            mapStrategy.setType(SpotLine.class);


/*
   String[] columns = new String[]{"idSofkiano", "nameSofkiano","idProject", "nameProject", "description", "hours", "minutes", "type"};
            mapStrategy.setColumnMapping(columns);
 */
            /*
                 val beanToCsv = StatefulBeanToCsvBuilder<T>(outputWriter)
                .withMappingStrategy(HeaderOrderingStrategy(clazz))
                .withApplyQuotesToAll(false)
                .withLineEnd("\n")
                .build()

            beanToCsv.write(scheduleSettings)
             */
            HeaderColumnNameMappingStrategy<SpotLine> headerColumnNameMappingStrategy =
                    new HeaderColumnNameMappingStrategyBuilder<SpotLine>()
                    .withForceCorrectRecordLength(true)
                    .build();
            headerColumnNameMappingStrategy.setType(SpotLine.class);
            headerColumnNameMappingStrategy.setColumnOrderOnWrite((o1, o2) -> {
                Map<String, Integer> columnOrders = Map.of("ID", 1, "NAME", 2, "PHONENUMBER", 3, "ADDRESS", 4);

                Integer o1Order = columnOrders.getOrDefault(o1, Integer.MAX_VALUE);
                Integer o2Order = columnOrders.getOrDefault(o2, Integer.MAX_VALUE);

                System.out.printf("Comparing: <%s> with <%s> %n", o1, o2);

                return o1Order.compareTo(o2Order);
            });


            ColumnPositionMappingStrategy<SpotLine> columnPositionMappingStrategy = new ColumnPositionMappingStrategy<>();
            columnPositionMappingStrategy.setType(SpotLine.class);
            String[] columns = new String[]{"id", "name", "phoneNumber", "address"}; // must be que properties name
            columnPositionMappingStrategy.setColumnMapping(columns);

            StatefulBeanToCsv<SpotLine> my =
                    new StatefulBeanToCsvBuilder<SpotLine>(writer)
                            .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                            //.withLineEnd(DEFAULT_LINE_END)
                            .withLineEnd(RFC4180_LINE_END)
                            .withSeparator(DEFAULT_SEPARATOR)
                            //.withMappingStrategy(columnPositionMappingStrategy)
                            .withMappingStrategy(headerColumnNameMappingStrategy)
                            .build();

            /// * *****

            my.write(workDoneList);

            return Flux.just(stringBuffer(writer.getBuffer().toString()));

        } catch (CsvException ex) {

            return Flux.error(ex.getCause());
        }
    }


    private static DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }

    public Flux<DataBuffer> generateCsv() {

        Mono<List<SpotLine>> allSpots =
                spotRepository.findAll()
                              .map(entity ->
                                      SpotLine.builder()
                                              .id(entity.getId())
                                              .name(entity.getName())
                                              .address(entity.getAddress())
                                              .phoneNumber(entity.getPhoneNumber())
                                              .build()
                              )
                              .collectList();

        return allSpots.flatMapMany(CsvDataWriterService::writeWorkDone);
    }

}
