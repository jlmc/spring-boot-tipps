package io.github.jlmc.springqualifiers.users.boundary;

import io.github.jlmc.springqualifiers.users.control.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/users")
public class UsersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersController.class);

    public final UserProvider admins;
    public final UserProvider guests;

    public final BeanFactory beanFactory;

    public UsersController(@Qualifier("Admin") UserProvider admins,
                           @Qualifier("Guest") UserProvider guests,
                           BeanFactory beanFactory) {
        this.admins = admins;
        this.guests = guests;
        this.beanFactory = beanFactory;
    }

    @GetMapping("/admins")
    public String admin() {
        return admins.getName() + " " + Instant.now();
    }

    @GetMapping("/guests")
    public String guest() {
        return guests.getName() + " " + Instant.now();
    }

    /**
     * curl localhost:8080/users/runtime/Guest
     * curl localhost:8080/users/runtime/Admin
     */
    @GetMapping("/runtime/{name}")
    public ResponseEntity<String> runtimeResolve(@PathVariable String name) {
        try {
            UserProvider provider = BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, UserProvider.class, name);
            return ResponseEntity.ok(provider.getName());
        } catch (BeansException e) {
            LOGGER.error("No available bean <{}> with the qualifier <{}>", UserProvider.class.getName(), name, e);
            return ResponseEntity.badRequest()
                                 .body(
                                         "No available bean <%s> with the qualifier <%s>".formatted(
                                                 UserProvider.class.getSimpleName(), name));
        }
    }
}
