package io.github.jlmc.sbvalidation.movies.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class Details {
    private String summary;
    @NotBlank
    private String mainActor;
    @Valid
    private List<@NotBlank String> actors;

    public Details(String summary, String mainActor, List<String> actors) {
        this.summary = summary;
        this.mainActor = mainActor;
        this.actors = actors;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getMainActor() {
        return mainActor;
    }

    public void setMainActor(String mainActor) {
        this.mainActor = mainActor;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }
}
