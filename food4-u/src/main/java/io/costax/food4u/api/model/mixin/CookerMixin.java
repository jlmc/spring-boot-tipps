package io.costax.food4u.api.model.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("cook")
public class CookerMixin {

    @JsonProperty("title")
    private String name;

    @JsonIgnore
    private int version;
}
