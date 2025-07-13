package com.amit.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter  { // this will ensure that the filter is executed once per request
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");
        if(jwt!=null){
            jwt = jwt.substring(7); // Remove "Bearer " prefix
            try{
                SecretKey key = Keys.hmacShaKeyFor(JWT_CONSTANT.SECRET_KEY.getBytes()); // Use the secret key to validate the JWT
                Claims claims = Jwts.parserBuilder().setSigningKey(key).build(). // Parse the JWT and extract claims, this is where the JWT is validated against the secret key
                        parseClaimsJws(jwt).getBody();

                String email = String.valueOf(claims.get("email"));
                String authorities = String.valueOf(claims.get("authorities")); // Extract the authorities from the claims (roles/permissions

                List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities); // Convert the authorities string to a list of GrantedAuthority
                Authentication authentication = new UsernamePasswordAuthenticationToken(email,null,auths); // Create an authentication object with the email and authorities
                SecurityContextHolder.getContext().setAuthentication(authentication); // Set the authentication in the security context so that it can be accessed later in the request processing pipeline and by other components of the application
            }
            catch(Exception e){
                // Handle JWT validation exceptions here
                throw new BadCredentialsException("Invalid JWT token", e);
            }
        }

        filterChain.doFilter(request,response); // Continue the filter chain to allow the request to proceed
    }
}
