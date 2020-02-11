package io.costax.versioningapis.uris.core.web;

import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;

@UtilityClass
public final class ApiExampleMediaTypes {

    public static final String V1_APPLICATION_JSON_VALUE = "application/vnd.example.v1+json";

    public static final MediaType V1_APPLICATION_JSON = MediaType.valueOf(V1_APPLICATION_JSON_VALUE);

    public static final String V2_APPLICATION_JSON_VALUE = "application/vnd.example.v2+json";

    public static final MediaType V2_APPLICATION_JSON = MediaType.valueOf(V2_APPLICATION_JSON_VALUE);

}
