package io.github.jlmc.korders.processor.domain.model.aggregates;

import jakarta.persistence.Embeddable;

@Embeddable
public class FailureRecordCause {

    private String className;
    private String message;

    public FailureRecordCause(String className, String message) {
        this.className = className;
        this.message = message;
    }

    public FailureRecordCause() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
