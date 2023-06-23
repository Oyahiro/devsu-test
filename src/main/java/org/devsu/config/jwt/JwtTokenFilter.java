package org.devsu.config.jwt;

import org.devsu.service.UserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Objects;

/**
 * Se ejecuta por cada petición para verificar si es válido el token
 */

public class JwtTokenFilter extends OncePerRequestFilter {

    private final Logger LOG = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserDetailService userDetailService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req,
                                    @NotNull HttpServletResponse res,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getToken(req);
            String requestUrl = req.getRequestURL().toString();
            if(Objects.nonNull(token) && jwtProvider.validateToken(token) && !requestUrl.contains("login")) {
                String username = jwtProvider.getUsernameFromToken(token);
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);
            }
        } catch (Exception e) {
            LOG.error("FAIL in doFilterInternal");
        } finally {
            filterChain.doFilter(req, res);
        }
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if(Objects.nonNull(header) && header.startsWith("Bearer")) {
            return header.replace("Bearer ", "");
        }
        return null;
    }

}
