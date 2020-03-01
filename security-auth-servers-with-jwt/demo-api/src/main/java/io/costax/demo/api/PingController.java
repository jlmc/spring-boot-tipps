package io.costax.demo.api;

import io.costax.demo.core.security.SecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    SecurityHelper securityHelper;

    //@PreAuthorize("hasAuthority('MANAGER_USERS')")
    //@PreAuthorize("isAuthenticated()")
    @PreAuthorize("hasAuthority('MANAGER_USERS')")
    @GetMapping
    public String ping() {
        return String.format("Ping - %s - %s ", securityHelper.getUserId(), System.currentTimeMillis() );
    }

}
