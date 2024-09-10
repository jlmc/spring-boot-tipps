package io.github.jlmc.poc.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/ping")
public class PingsController {

    @Autowired
    Clock clock;

    @GetMapping(path = "/ping")
    public String ping() {
        return "pong - " + ZonedDateTime.now(clock);
    }
}
