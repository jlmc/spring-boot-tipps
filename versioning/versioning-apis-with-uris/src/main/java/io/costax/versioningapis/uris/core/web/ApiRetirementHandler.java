package io.costax.versioningapis.uris.core.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Component
public class ApiRetirementHandler extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        if (request.getRequestURI().startsWith("/v1/")) {
            response.setStatus(HttpStatus.GONE.value());
            return false;
        }

        return true;
    }
}
