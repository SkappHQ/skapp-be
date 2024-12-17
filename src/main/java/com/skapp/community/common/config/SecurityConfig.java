package com.skapp.community.common.config;

import com.skapp.community.common.component.AuthEntryPoint;
import com.skapp.community.common.component.ExceptionLoggingFilter;
import com.skapp.community.common.component.JwtAuthFilter;
import com.skapp.community.common.component.ResetDatabaseApiKeyFilter;
import com.skapp.community.common.constant.AuthConstants;
import com.skapp.community.common.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@ConditionalOnMissingBean(name = "EPSecurityConfig")
public class SecurityConfig {

	@NonNull
	private final JwtAuthFilter jwtAuthFilter;

	@NonNull
	private final UserService userService;

	@NonNull
	private final AuthEntryPoint authEntryPoint;

	@NonNull
	private final ExceptionLoggingFilter exceptionLoggingFilter;

	@NonNull
	private final ResetDatabaseApiKeyFilter resetDatabaseApiKeyFilter;

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService.userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults());
		http.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
			.exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/v1/auth/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
						"/v1/app-setup-status", "/robots.txt", "/ws/**", "/v1/auth/forgot/password",
						"/v1/people/search/email-exists", "/health")
				.permitAll()
				.requestMatchers("/v1/reset-database")
				.permitAll()
				.anyRequest()
				.authenticated());

		http.addFilterBefore(exceptionLoggingFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(resetDatabaseApiKeyFilter, UsernamePasswordAuthenticationFilter.class);
		http.authenticationProvider(authenticationProvider());

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration
			.setAllowedHeaders(Arrays.asList("authorization", "content-type", AuthConstants.RESET_DATABASE_API_HEADER));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
