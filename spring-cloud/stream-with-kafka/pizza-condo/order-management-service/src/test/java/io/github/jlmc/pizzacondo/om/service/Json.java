package io.github.jlmc.pizzacondo.om.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.Instant;

public class Json {

    private static Parser parser = new Parser();

    public static String toJson(Object object) {
        return parser.toJson(object);
    }

    public static <T> T parse(String json, Class<T> type) {
        return parser.parse(json, type);
    }

    public static class Parser {

        final ObjectMapper objectMapper;

        Parser() {
            ObjectMapper om = new ObjectMapper();
            om = om.findAndRegisterModules();

            //om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            om = om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


            this.objectMapper = om;
        }

        public String toJson(Object object) {
            try {
                return this.objectMapper.writeValueAsString(object);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public <T> T parse(String json, Class<T> type) {
            try {
                return this.objectMapper.readValue(json, type);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
