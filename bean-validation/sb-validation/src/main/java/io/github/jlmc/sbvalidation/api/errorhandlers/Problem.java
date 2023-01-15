package io.github.jlmc.sbvalidation.api.errorhandlers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Problem {
    private String x = "X";
    private Instant timestamp;
    private String path;
    private int status = 500;
    private String error;
    private String message;
    private List<Field> fields = null;

    private Problem(Instant timestamp, String path, int status, String error, String message) {
        this.timestamp = timestamp;
        this.path = path;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public static Problem createProblem(Instant timestamp, String path, int status, String error, String message) {
        return new Problem(timestamp, path, status, error, message);
    }

    public Problem addFieldError(String field, String message) {
        if (fields == null) this.fields = new ArrayList<>();
        this.fields.add(new Field(field, message));
        return this;
    }

    public Problem setFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }

    public static class Field {
        private String field;
        private String message;

        public Field(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
