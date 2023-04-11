package io.github.jlmc.uploadcsv.heroes.boundary;

import io.swagger.v3.oas.annotations.media.Schema;

public class ApiDocs {

    public static final String EXAMPLE = """
            LOCATION_ID,LOCATION_NAME,ADDRESS_STREET, ADDRESS_ZIP_CODE, ADDRESS_CITY, ADDRESS_REGION, COUNTRY_NAME, CONTACT_PHONE, CONTACT_EMAIL, IMAGE_URL, TIMEZONE, BUSINESS_MON, BUSINESS_TUE, BUSINESS_WED, BUSINESS_THU, BUSINESS_FRI, BUSINESS_SAT, BUSINESS_SUN
            123,South Lyon,796 Hillcrest Ave.,48178, Los Angeles, California, United States, +12029182132, john-doe@example.com, https://example-123.com,America/Los_Angeles,10:00am-01:00pm | 02:00pm-08pm,10:00am-01:00pm | 02:00pm-08pm,10:00am-01:00pm | 02:00pm-08pm,10:00am-01:00pm | 02:00pm-08pm,10:00am-01:00pm | 02:00pm-08pm,10:00am-01:00pm | 02:00pm-08pm,10:00am-01:00pm
            124,New Milford,703 Forest Avenue,06776,Chicago,Illinois, United States,+15056468404, john-doe@example.com, https://example-124.com,America/Chicago,10:00am-01:00pm | 02:00pm-08pm,"",10:00am-01:00pm | 02:00pm-08pm,10:00am-01:00pm | 02:00pm-08pm,10:00am-01:00pm | 02:00pm-08pm,"",""
            """;


    @SuppressWarnings("unused")
    @Schema(name = "request data")
    static final class ApiContent {
        @Schema(name = "file",
                example = EXAMPLE,
                maximum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "string", format = "binary")
        public String file;
    }
}
