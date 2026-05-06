package com.ecommerce.filter;

import com.ecommerce.util.JwtUtil;
import com.ecommerce.util.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class AuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/home", "/product", "/login", "/signup", "/css/", "/js/", "/images/", "/error"
    );

    private static final List<String> PUBLIC_EXACT = Arrays.asList(
            "/", ""
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Rate limiting check
        String clientIp = httpRequest.getRemoteAddr();
        if (!RateLimiter.isAllowed(clientIp)) {
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Too many requests. Please try again later.");
            return;
        }

        // Allow public paths without authentication
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Check authentication via JWT token or session
        boolean authenticated = false;

        // Try JWT token from header
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (JwtUtil.isTokenValid(token)) {
                httpRequest.setAttribute("userId", JwtUtil.getUserId(token));
                httpRequest.setAttribute("username", JwtUtil.getUsername(token));
                httpRequest.setAttribute("isAdmin", JwtUtil.isAdmin(token));
                authenticated = true;
            }
        }

        // Fall back to session-based auth
        if (!authenticated) {
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                Integer userId = (Integer) session.getAttribute("userId");
                if (userId != null) {
                    httpRequest.setAttribute("userId", userId);
                    httpRequest.setAttribute("username", session.getAttribute("username"));
                    httpRequest.setAttribute("isAdmin", session.getAttribute("isAdmin"));
                    authenticated = true;
                }
            }
        }

        if (!authenticated) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AuthFilter destroyed");
    }

    private boolean isPublicPath(String path) {
        if (PUBLIC_EXACT.contains(path)) {
            return true;
        }
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath) || path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }
}
