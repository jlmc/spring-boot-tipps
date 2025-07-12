package io.github.jlmc.reactordemo.models;

public class EventSummary {
    final String id;
    final String title;
    final String departmentName;
    final String providerName;

    public EventSummary(String id, String title, String departmentName, String providerName) {
        this.id = id;
        this.title = title;
        this.departmentName = departmentName;
        this.providerName = providerName;
    }

    @Override
    public String toString() {
        return "EventSummary{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", providerName='" + providerName + '\'' +
                '}';
    }
}
