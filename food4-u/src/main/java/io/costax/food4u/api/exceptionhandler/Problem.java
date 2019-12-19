package io.costax.food4u.api.exceptionhandler;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Problem {

    private LocalDateTime dateTime;
    private String message;



}
