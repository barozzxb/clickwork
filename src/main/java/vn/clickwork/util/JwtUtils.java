package vn.clickwork.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import vn.clickwork.enumeration.ERole;

@Component
public class JwtUtils {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration.ms}")
	private int jwtExpirationMs;

	public String generateToken(String username, ERole role) {
		Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		return Jwts.builder()
				.setSubject(username)
				.claim("role", role.name())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.signWith(key, SignatureAlgorithm.HS512)
				.compact();
	}

	// Lấy username từ token
	public String getUsernameFromJwtToken(String token) {
		Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		return Jwts
				.parserBuilder()
				.setSigningKey(key)
				.build().parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	// Xác thực token (cập nhật để dùng parserBuilder)
	public boolean validateJwtToken(String token) {
		try {
			Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			System.err.println("Invalid JWT signature: " + e.getMessage());
			return false;
		}
	}
}
