package io.costax.examplesapi.beans.profile;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/profile-bean-resolution")
public class ProfileBeanResolutionResource {

    @Autowired
    Foo foo;

    @GetMapping
    @ResponseBody
    public String hello() {
        return foo.message();
    }
}
