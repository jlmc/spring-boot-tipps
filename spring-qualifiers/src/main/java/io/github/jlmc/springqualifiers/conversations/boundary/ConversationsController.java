package io.github.jlmc.springqualifiers.conversations.boundary;

import io.github.jlmc.springqualifiers.conversations.control.ConversationProvider;
import io.github.jlmc.springqualifiers.conversations.control.ConversationType;
import io.github.jlmc.springqualifiers.conversations.control.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Validated
@RestController
@RequestMapping("/conversations")
public class ConversationsController {

    @Autowired
    @Conversations(type = ConversationType.SMS, format = "text")
    ConversationProvider sms;

    @Autowired
    @Conversations(type = ConversationType.SOCIAL_MEDIA, format = "data")
    ConversationProvider social;

    @Autowired
    ApplicationContext applicationContext;

    @GetMapping
    public List<String> conversations() {
        return Stream.concat(sms.fetch().stream(), social.fetch().stream()).toList();
    }

    @GetMapping("/{type}/{format}")
    public List<String> conversations(
            @PathVariable @Pattern(regexp = "SMS|SOCIAL_MEDIA") String type,
            @PathVariable @Pattern(regexp = "text|data") String format) {
        //BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, ConversationProvider.class, "Qualifier-Value");
        return resolveProvider(ConversationType.valueOf(type), format).stream().flatMap(it -> it.fetch().stream()).toList();
    }

    public Optional<ConversationProvider> resolveProvider(ConversationType type, String format) {
        Map<String, ConversationProvider> beanMap = applicationContext.getBeansOfType(ConversationProvider.class);
        return beanMap.entrySet()
                      .stream()
                      .filter(entry -> {
                          Conversations annotation = applicationContext.findAnnotationOnBean(entry.getKey(), Conversations.class);

                          return annotation != null && annotation.format().equals(format) && annotation.type().equals(type);
                      }).map(Map.Entry::getValue)
                      .findAny();
    }
}
