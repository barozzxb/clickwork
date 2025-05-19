package vn.clickwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfigurationSource;
import vn.clickwork.filter.JwtFilter;
import vn.clickwork.service.impl.CustomAccountDetailsService;
import vn.clickwork.util.JwtUtils;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomAccountDetailsService accDetailServ;
	private final JwtUtils jwtUtils;
	private final CorsConfigurationSource corsConfigurationSource;

	public SecurityConfig(CustomAccountDetailsService accDetailServ, JwtUtils jwtUtils,
			CorsConfigurationSource corsConfigurationSource) {
		this.accDetailServ = accDetailServ;
		this.jwtUtils = jwtUtils;
		this.corsConfigurationSource = corsConfigurationSource;
	}

	@Bean
	public JwtFilter jwtFilter() {
		return new JwtFilter(jwtUtils, accDetailServ);
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(accDetailServ);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/").permitAll()
						.requestMatchers("/api/jobs/**").permitAll()
						.requestMatchers("/uploads/**").permitAll() 
						.requestMatchers("/api/support/**").permitAll() 
						.requestMatchers("/api/saved-jobs/**").hasRole("APPLICANT")
						.requestMatchers("/api/applications/**").permitAll() 
						.requestMatchers("/api/search/**").permitAll()
						.requestMatchers("/api/applicant/**").hasRole("APPLICANT")
						.requestMatchers("/api/employer/**").permitAll()
						.requestMatchers("/employer/**").hasRole("EMPLOYER")
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
						.requestMatchers(HttpMethod.DELETE, "/api/applicant/manage-cvs/delete/**").authenticated()
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/support/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}