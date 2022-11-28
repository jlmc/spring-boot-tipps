package io.github.jlmc.aop.domain.model;

import lombok.Data;

import java.util.Map;

@Data
public class TodoSearchFilter {

    String perPage;
    Map<String, String[]> reqParam;
}
