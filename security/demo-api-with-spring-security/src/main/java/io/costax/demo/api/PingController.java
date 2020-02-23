package io.costax.demo.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/ping",
        produces = {
                MediaType.APPLICATION_JSON_VALUE
        })
public class PingController {

    @GetMapping
    public String ping() {
        return "Ping-" + System.currentTimeMillis();
    }

}
