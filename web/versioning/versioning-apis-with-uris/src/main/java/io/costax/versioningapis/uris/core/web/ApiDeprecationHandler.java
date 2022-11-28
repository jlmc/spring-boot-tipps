package io.costax.versioningapis.uris.core.web;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ApiDeprecationHandler extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        if (request.getRequestURI().startsWith("/v1/")) {
            response.addHeader("X-Example-Deprecated",
                    "This version of the API is depreciated and will cease to exist as of 01/01/2021. " +
                            "Use the most current version of the API.");
        }

        return true;
    }
}
