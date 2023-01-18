package io.github.jlmc.sbvalidation.api.errorhandlers;

import java.util.ArrayList;
import java.util.List;

public class Problem {
    private String path;
    private int status = 500;
    private String error;
    private String message;
    private List<Field> fields = null;

    private Problem(String path, int status, String error, String message) {
        this.path = path;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public static Problem createProblem(String path, int status, String error, String message) {
        return new Problem( path, status, error, message);
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

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Field> getFields() {
        return fields;
    }
}
