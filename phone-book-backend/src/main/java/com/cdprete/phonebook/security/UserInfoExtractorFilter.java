package com.cdprete.phonebook.security;

import com.cdprete.phonebook.api.security.BaseUserInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.util.StringUtils.hasText;

/**
 * @author Cosimo Damiano Prete
 * @since 11/02/2022
 */
@Component
class UserInfoExtractorFilter extends OncePerRequestFilter {
    // TODO make this configurable
    private static final String AUTH_HEADER_NAME = "X-Auth-User";

    private final SecurityContextHolder securityContextHolder;

    UserInfoExtractorFilter(SecurityContextHolder securityContextHolder) {
        this.securityContextHolder = securityContextHolder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        var headerValue = request.getHeader(AUTH_HEADER_NAME);
        if(hasText(headerValue)) {
            try {
                securityContextHolder.runWithSecurityContext(new BaseUserInfo(headerValue), () -> super.doFilter(request, response, chain));
            } catch (IOException | ServletException e) {
                throw e;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            super.doFilter(request, response, chain);
        }
    }
}
