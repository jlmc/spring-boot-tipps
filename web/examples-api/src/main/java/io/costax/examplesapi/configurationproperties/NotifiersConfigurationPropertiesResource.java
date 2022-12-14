package io.costax.examplesapi.configurationproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller()
@RequestMapping(value = "configuration-properties")
public class NotifiersConfigurationPropertiesResource {

    @Autowired
    NotifiersConfigurationProperties configurationProperties;

    @GetMapping
    @ResponseBody
    public String hello() {
        return "hello - " +
                "" + configurationProperties.getSmtpHost() + ":" + configurationProperties.getSmtpPort();
    }

    @GetMapping(value = "/others", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Others getOthers() {
        return configurationProperties.getOthers();
    }
}
