package io.github.jlmc.springqualifiers.conversations.control;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Conversations(type = ConversationType.SMS, format = "text")
public class SMSConversations implements ConversationProvider{

    @Override
    public List<String> fetch() {
        return List.of("SMS: 1", "SMS: 2");
    }
}
