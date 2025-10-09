package hcmute.edu.vn.techstore.config;

import hcmute.edu.vn.techstore.service.impl.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .headers(header ->
                        header.httpStrictTransportSecurity(hsts ->
                                        hsts.includeSubDomains(true)
                                                .maxAgeInSeconds(31536000)//1 year
                                )
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                                .contentTypeOptions(contentTypeOptions -> {})
                                .xssProtection(HeadersConfigurer.XXssConfig::disable))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/register","/uploads/**","/web/assert/**","/forgot-password","/products","/products/**", "/forgot-password","/api/products/search","/about-us")
                        .permitAll()
                        .requestMatchers("/web/**")
                        .hasAnyRole("CUSTOMER", "ADMIN", "STAFF")
                        .requestMatchers("/staff/**", "/admin/asset/**")
                        .hasAnyRole("STAFF","ADMIN")
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
}
