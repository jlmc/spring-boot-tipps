package io.github.jlmc.springqualifiers.conversations.control;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Conversations(type = ConversationType.SOCIAL_MEDIA, format = "data")
public class SocialMediaConversations implements ConversationProvider{
    @Override
    public List<String> fetch() {
        return List.of("Twitter: 1", "Twitter: 2", "Google: 1");
    }
}
