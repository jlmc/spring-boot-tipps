package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Columns {

    public static final String LOCATION_ID = "LOCATION_ID";
    public static final String LOCATION_NAME = "LOCATION_NAME";
    public static final String ADDRESS_STREET = "ADDRESS_STREET";
    public static final String ADDRESS_ZIP_CODE = "ADDRESS_ZIP_CODE";
    public static final String ADDRESS_CITY = "ADDRESS_CITY";
    public static final String ADDRESS_REGION = "ADDRESS_REGION";
    public static final String COUNTRY_NAME = "COUNTRY_NAME";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String IMAGE_URL = "IMAGE_URL";
    public static final String CONTACT_PHONE = "CONTACT_PHONE";
    public static final String TIMEZONE = "TIMEZONE";
    public static final String BUSINESS_MON = "BUSINESS_MON";
    public static final String BUSINESS_TUE = "BUSINESS_TUE";
    public static final String BUSINESS_WED = "BUSINESS_WED";
    public static final String BUSINESS_THU = "BUSINESS_THU";
    public static final String BUSINESS_FRI = "BUSINESS_FRI";
    public static final String BUSINESS_SAT = "BUSINESS_SAT";
    public static final String BUSINESS_SUN = "BUSINESS_SUN";

    private static final List<String> COLUMNS = List.of(
            LOCATION_ID,
            LOCATION_NAME,
            ADDRESS_STREET,
            ADDRESS_ZIP_CODE,
            ADDRESS_CITY,
            ADDRESS_REGION,
            COUNTRY_NAME,
            LATITUDE,
            LONGITUDE,
            IMAGE_URL,
            CONTACT_PHONE,
            TIMEZONE,
            BUSINESS_MON,
            BUSINESS_TUE,
            BUSINESS_WED,
            BUSINESS_THU,
            BUSINESS_FRI,
            BUSINESS_SAT,
            BUSINESS_SUN
    );

    public static final Map<String, Integer> COLUMN_INDEXES =
            COLUMNS.stream()
                   .map(it -> Map.entry(it, COLUMNS.indexOf(it)))
                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static Integer indexOf(String columnName) {
        return COLUMN_INDEXES.getOrDefault(columnName, Integer.MAX_VALUE);
    }
}
