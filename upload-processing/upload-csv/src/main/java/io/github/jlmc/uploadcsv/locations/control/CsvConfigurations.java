package io.github.jlmc.uploadcsv.locations.control;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import io.github.jlmc.uploadcsv.locations.entity.Location;

import java.util.List;

class CsvConfigurations {
    static HeaderColumnNameMappingStrategy<Location> getLocationHeaderColumnNameMappingStrategy() {
        final List<String> columnsOrder = List.of(
                "ID",
                "NAME",
                "PHONENUMBER",
                "IMAGEURL",
                "TIMEZONE",
                "ADDRESS_ADDRESS",
                "ADDRESS_CITY",
                "ADDRESS_ZIP_CODE",
                "ADDRESS_COUNTRY_NAME",
                "ADDRESS_REGION_NAME",
                "LATITUDE",
                "LONGITUDE",
                "BUSINESS_MON"
        );

        HeaderColumnNameMappingStrategy<Location> headerColumnNameMappingStrategy =
                new HeaderColumnNameMappingStrategyBuilder<Location>()
                        .withForceCorrectRecordLength(true)
                        .build();
        headerColumnNameMappingStrategy.setType(Location.class);

        headerColumnNameMappingStrategy.setColumnOrderOnWrite((o1, o2) -> {
            Integer index1 = columnsOrder.indexOf(o1);
            Integer index2 = columnsOrder.indexOf(o2);
            index1 = index1 < 0 ? Integer.MAX_VALUE : index1;
            index2 = index2 < 0 ? Integer.MAX_VALUE : index2;

            System.out.printf("Comparing: <%s> with <%s> %n", o1, o2);

            return index1.compareTo(index2);
        });

        return headerColumnNameMappingStrategy;
    }
}
