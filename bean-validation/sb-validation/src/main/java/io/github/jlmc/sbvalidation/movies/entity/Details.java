package io.github.jlmc.sbvalidation.movies.entity;

import java.util.List;

public class Details {
    private String summary;
    private String mainActor;
    private List<String> actors;

    public Details(String summary, String mainActor, List<String> actors) {
        this.summary = summary;
        this.mainActor = mainActor;
        this.actors = actors;
    }
}
