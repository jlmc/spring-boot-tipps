package io.costax.examplesapi.beans.primary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping(value = "/primary-annotation")
public class PrimaryAnnotationResource {

    @Autowired
    Flying flying;

    @GetMapping
    @ResponseBody
    public String hello(@RequestParam(name = "id", required = false) String id) {
        final String s = Optional.ofNullable(id).orElse("2.17. Beans disambiguation with @Primary");

        return flying.fly(s);
    }
}
