package hcmute.edu.vn.techstore.config;

import hcmute.edu.vn.techstore.service.impl.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private final CustomSuccessHandle customSuccessHandle;

    private final UserDetailServiceImpl userDetailServiceImpl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> {
                    CookieCsrfTokenRepository repository = new CookieCsrfTokenRepository();
                    repository.setCookieCustomizer(customizer -> customizer.sameSite("Lax")
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(3600));
                    repository.setCookieName("XSRF-TOKEN");
                    csrf.csrfTokenRepository(repository);
                })
                .headers(header ->
                        header.httpStrictTransportSecurity(hsts ->
                                        hsts.includeSubDomains(true)
                                                .maxAgeInSeconds(31536000)
                                )
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                                .xssProtection(HeadersConfigurer.XXssConfig::disable).contentSecurityPolicy(csp -> csp
                                        .policyDirectives("default-src 'self'; " +
                                                "script-src 'self'; " +
                                                "style-src 'self'; " +
                                                "img-src 'self' data: res.cloudinary.com; " +
                                                "font-src 'self'; " +
                                                "connect-src 'self'; " +
                                                "form-action 'self'; " +
                                                "frame-ancestors 'none'; " +
                                                "object-src 'none';")
                                ))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/register", "/uploads/**", "/web/assert/**", "/forgot-password", "/products", "/products/**", "/forgot-password", "/api/products/search", "/about-us")
                        .permitAll()
                        .requestMatchers("/web/**")
                        .hasAnyRole("CUSTOMER", "ADMIN", "STAFF")
                        .requestMatchers("/staff/**", "/admin/asset/**")
                        .hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProcess")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(customSuccessHandle)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .permitAll()
                )
                .userDetailsService(userDetailServiceImpl)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(10));
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowCredentials(true);
        cfg.setAllowedOrigins(List.of("https://localhost:8443"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Content-Type","Authorization"));
        cfg.setExposedHeaders(List.of("Content-Length","Retry-After"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cfg);
        return source;
    }
}
