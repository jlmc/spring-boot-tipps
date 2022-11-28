package io.costax.demo.api;

import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;

public final class ResourceUriHelper {

    private ResourceUriHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void addUriInResponseHeader(Object resourceId) {
        URI uri = getUri(resourceId);

        HttpServletResponse response = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getResponse();

        response.setHeader(HttpHeaders.LOCATION, uri.toString());
    }

    public static URI getUri(final Object resourceId) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(resourceId).toUri();
    }

}
