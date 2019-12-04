package io.costax.examplesapi;

import io.costax.examplesapi.di.boundary.ClientActivator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyFirstController {

    private final ClientActivator clientActivator;

    public MyFirstController(final ClientActivator clientActivator) {
        this.clientActivator = clientActivator;
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {

        clientActivator.active(1L);

        return "hello - " + System.currentTimeMillis();
    }

}
