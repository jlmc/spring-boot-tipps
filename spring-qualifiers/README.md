# Spring Boot @Qualifier


> Spring Boot @Qualifier tutorial shows how to differentiate beans of the same type with @Qualifier. It can also be used to annotate other custom annotations that can then be used as qualifiers.
> 

- [qualifier](https://zetcode.com/springboot/qualifier/)


## Differentiating UserProvider beans
In our application, we have two beans of UserProvider type: GuestProvider and AdminProvider. We use the @Qualifier annotation to distinguish between them.

```
public interface UserProvider {
    String getName();
}

@Component
@Qualifier("Admin")
public class AdminProvider implements UserProvider {
    @Override
    public String getName() {
        return "Admin";
    }
}

@Component
@Qualifier("Guest")
public class GuestProvider implements UserProvider {
    @Override
    public String getName() {
        return "Guest";
    }
}

@RestController
@RequestMapping("/users")
public class UsersController {

    public final UserProvider admins;
    public final UserProvider guests;

    public UsersController(@Qualifier("Admin") UserProvider admins,
                           @Qualifier("Guest") UserProvider guests) {
        this.admins = admins;
        this.guests = guests;
    }

    @GetMapping("/admins")
    public String admin() {
        return admins.getName() + " " + Instant.now();
    }

    @GetMapping("/guests")
    public String guest() {
        return guests.getName() + " " + Instant.now();
    }
}
```


## Using factory to create beans

```shell
curl -v localhost:8080/appointments | jq .

```

```shell
curl -v -X POST -L localhost:8080/appointments -H "Content-Type: application/json" \
  -d'{ "departmentId": "123", "patientId": "77"}' | jq .
```

When we use a configuration factory method we can use the @Qualifier annotation in each bean method.
However, the use of the annotations in the factory method is not required, the bean can be resolved with method name `billing` or `reportingEventPublisher` as @Qualifier value.

```java
@Configuration
public class EventPublisherConfiguration {

    @Bean
    //@Qualifier("billing")
    public EventPublisher billing() {
        return new EventPublisher() {
            @Override
            public String publisher(String operation) {
                String s = "pushing Billing: " + operation;
                System.out.println(s);
                return s;
            }
        };
    }

    @Bean
    @Qualifier("reporting")
    public EventPublisher reportingEventPublisher() {
        return new EventPublisher() {
            @Override
            public String publisher(String operation) {
                String s = "pushing Reporting: " + operation;
                System.out.println(s);
                return s;
            }
        };
    }
}
```

## Creating custom @Qualifier annotation

### Dedicated annotation

```java
@Qualifier
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConversationSMS {
}

@Component
@ConversationSMS
public class SMSConversations implements ConversationProvider{}

@Component
@ConversationSocial
public class SocialMediaConversations implements ConversationProvider{}

@RestController
@RequestMapping("/conversations")
public class ConversationsController {

    @Autowired
    @ConversationSMS
    ConversationProvider sms;

    @Autowired
    @ConversationSocial
    ConversationProvider social;

    @GetMapping
    public List<String> conversations() {
        return Stream.concat(sms.fetch().stream(), social.fetch().stream()).toList();
    }
}
```
