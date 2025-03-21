package io.github.jlmc.sb.app.mvc.interfaces.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class TodoRepresentation {

    private Integer id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @Min(1)
    @Max(5)
    private Integer issuePriority = 1;

    public TodoRepresentation() {
    }

    private TodoRepresentation(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public static TodoRepresentation of(String title, String description) {
        return new TodoRepresentation(title, description);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIssuePriority() {
        return issuePriority;
    }

    public void setIssuePriority(Integer issuePriority) {
        this.issuePriority = issuePriority;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
