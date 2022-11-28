package io.github.jlmc.springqualifiers.users.control;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("Admin")
public class AdminProvider implements UserProvider {
    @Override
    public String getName() {
        return "Admin";
    }
}
