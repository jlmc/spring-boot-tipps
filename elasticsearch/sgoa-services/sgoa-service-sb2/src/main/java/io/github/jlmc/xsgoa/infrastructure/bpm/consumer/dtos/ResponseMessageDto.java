package io.github.jlmc.xsgoa.infrastructure.bpm.consumer.dtos;

import lombok.Data;

@Data
public class ResponseMessageDto<T> {

    private T data;
}
