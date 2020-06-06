package nl.reservefast.authservice.config;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class HttpsEnforcer implements Filter {
    private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getHeader(X_FORWARDED_PROTO) != null && request.getHeader(X_FORWARDED_PROTO).indexOf("https") != 0) {
            String pathInfo = (request.getPathInfo() != null) ? request.getPathInfo() : "";
            response.sendRedirect("https://" + request.getServerName() + pathInfo);
            return;
        }

        filterChain.doFilter(request, response);
    }
}