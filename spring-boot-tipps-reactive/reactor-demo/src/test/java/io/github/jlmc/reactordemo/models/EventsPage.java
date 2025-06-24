package io.github.jlmc.reactordemo.models;

import java.util.List;

public class EventsPage {
    final int total;
    final List<Event> events;

    public EventsPage(List<Event> events) {
        this.events = events;
        this.total = events.size();
    }

    public int getTotal() {
        return total;
    }

    public List<Event> getEvents() {
        return List.copyOf(events);
    }
}


