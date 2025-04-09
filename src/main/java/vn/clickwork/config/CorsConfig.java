package vn.clickwork.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	private final String[] allowedOrigin = {"http://localhost:3000", 
											"http://localhost:9000", 
											"https://clickwork.vn", 
											"https://www.clickwork.vn"};
	
//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new org.springframework.web.servlet.config.annotation.WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
//				registry.addMapping("/**")
//						.allowedOrigins(allowedOrigin)
//						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//						.allowedHeaders("*")
//						.allowCredentials(true);
//			}
//		};
//	}
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:3000", 
            "http://localhost:9000", 
            "https://clickwork.vn", 
            "https://www.clickwork.vn"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
