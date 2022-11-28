package io.costax.springemaildemos.domain.notifications;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;
import java.util.Map;

public interface EmailSenderService {

    void send(Message message);

    @Getter
    @Builder
    class Message {

        @NonNull
        private String from;
        @NonNull
        @Singular
        private List<String> tos;
        @NonNull
        private String subject;
        @NonNull
        private String body;

        private String templateName;
        @Singular
        private Map<String, Object> templateParams;

    }
}
