package io.costax.examplesapi.qualifier;

public interface DocumentSerializer {

    String serialize(String id);

    String contentType();
}
