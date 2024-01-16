package io.github.jlmc.docsprocessing.service.commons.gateway.response;

import lombok.Data;

@Data
public class ResponseMessageDto<T> {

    private String title;
    private T data;
}
