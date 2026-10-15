package com.anipick.backend.common.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCRequestLoggingFilter implements Filter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String REQUEST_ID_KEY = "request_id";
    private static final int MAX_REQUEST_ID_LENGTH = 128;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        try {
            final String requestId = resolveRequestId(request);
            MDC.put(REQUEST_ID_KEY, requestId);
            if (response instanceof HttpServletResponse httpResponse) {
                httpResponse.setHeader(REQUEST_ID_HEADER, requestId);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.remove(REQUEST_ID_KEY);
            MDC.clear();
        }
    }

    private String resolveRequestId(final ServletRequest request) {
        if (request instanceof HttpServletRequest httpRequest) {
            final String header = httpRequest.getHeader(REQUEST_ID_HEADER);
            if (StringUtils.hasText(header) && header.length() <= MAX_REQUEST_ID_LENGTH) {
                return header.trim();
            }
        }
        return UUID.randomUUID().toString();
    }
}
