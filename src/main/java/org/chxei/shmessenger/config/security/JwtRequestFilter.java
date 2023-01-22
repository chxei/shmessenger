package org.chxei.shmessenger.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.chxei.shmessenger.service.UserService;
import org.chxei.shmessenger.utils.JwtUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;


public class JwtRequestFilter {
    private final UserService userService;

    private final JwtUtils jwtUtils;

    public JwtRequestFilter(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }


    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        try {

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtils.extractUsername(jwt);
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);
                if (jwtUtils.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        } catch (Exception e) {
//            CustomResponseEntity customResponseEntity = new CustomResponseEntity(ResponseCode.WRONG_JWT);
//            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
//            response.getOutputStream().print(new ObjectMapper().writeValueAsString(customResponseEntity));
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH, HEAD");
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Access-Control-Allow-Headers, Content-Type, Authorization, Origin, Accept");
        filterChain.doFilter(request, response);
    }
}
