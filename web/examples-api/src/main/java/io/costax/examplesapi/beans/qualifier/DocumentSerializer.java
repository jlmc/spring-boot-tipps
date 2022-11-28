package io.costax.examplesapi.beans.qualifier;

public interface DocumentSerializer {

    String serialize(String id);

    String contentType();
}
