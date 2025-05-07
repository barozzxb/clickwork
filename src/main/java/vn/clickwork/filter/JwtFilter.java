package vn.clickwork.filter;

import java.io.IOException;
import java.security.Key;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.clickwork.service.impl.CustomAccountDetailsService;
import vn.clickwork.util.JwtUtils;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
    private final CustomAccountDetailsService accountDetailsService;

    public JwtFilter(JwtUtils jwtUtils, CustomAccountDetailsService accountDetailsService) {
        this.jwtUtils = jwtUtils;
        this.accountDetailsService = accountDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromJwtToken(jwt);
                var userDetails = accountDetailsService.loadUserByUsername(username);
                
                Key key = Keys.hmacShaKeyFor(jwtUtils.getSecretKeyBytes()); // bạn có thể thêm method getSecretKeyBytes() vào JwtUtils
    	        Claims claims = Jwts.parserBuilder()
    	        	.setSigningKey(key)
    	                .build()
    	                .parseClaimsJws(jwt)
    	                .getBody();
    	
    	            String role = claims.get("role", String.class);
    	            List<GrantedAuthority> authorities = List.of(
    	                new SimpleGrantedAuthority("ROLE_" + role) // ví dụ: ROLE_APPLICANT
    	            );


                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Đặt vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            System.err.println("Không thể thiết lập xác thực người dùng: " + e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
