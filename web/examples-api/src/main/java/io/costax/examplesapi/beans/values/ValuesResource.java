package io.costax.examplesapi.beans.values;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/values")
public class ValuesResource {

    @Value("${app.example.property:'my-default-value'}")
    private String anyThing;

    @GetMapping
    @ResponseBody
    public String hello() {
        return "The property value is: " + anyThing;
    }
}
