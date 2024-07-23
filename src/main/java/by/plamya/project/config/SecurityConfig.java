package by.plamya.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import by.plamya.project.security.JWTAuthenticationEntryPoint;
import by.plamya.project.security.JWTAuthenticationFilter;
import by.plamya.project.security.oauth.OAuth2SuccessHandler;

import by.plamya.project.service.impl.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, proxyTargetClass = true)
@AllArgsConstructor
public class SecurityConfig {

    private JWTAuthenticationFilter jwtAuthenticationFilter;
    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private CustomUserDetailsService customUserDetailsService;

    private ClientRegistrationRepository сlientRegistrationRepository;
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    // Configuring HttpSecurity
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Configuring HttpSecurity...");
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> {

                    auth.requestMatchers("/api/auth/**", "/oauth2/**").permitAll();
                    auth.requestMatchers("/api/admin/**").hasAnyRole("ADMIN",
                            "INSTRUCTOR");
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                        jwtAuthenticationEntryPoint))
                .oauth2Login(auth -> auth.clientRegistrationRepository(сlientRegistrationRepository)
                        .successHandler(oAuth2SuccessHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        log.info("HttpSecurity configured successfully.");
        return httpSecurity.build();
    }

    // Password Encoding
    @Bean
    protected BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManagerBuilder
    @Bean
    protected DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        log.info("DaoAuthenticationProvider bean created.");
        return authProvider;
    }

    // Export AuthenticationManager bean
    @Bean
    protected AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info("Creating AuthenticationManager bean.");
        return config.getAuthenticationManager();
    }

    // @Bean
    // protected ClientRegistrationRepository clientRepository() {

    // ClientRegistration googleRegistration =
    // CommonOAuth2Provider.GOOGLE.getBuilder("google")
    // .clientId("id")
    // .clientSecret("secret")
    // .build();

    // // ClientRegistration facebookRegistration =
    // // CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
    // // .clientId("id")
    // // .clientSecret("secret")
    // // .build();

    // return new InMemoryClientRegistrationRepository(googleRegistration);
    // }

    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies("INSTRUCTOR")
                .role("INSTRUCTOR").implies("USER")
                .role("USER").implies("GUEST")
                .build();
    }

    // and, if using pre-post method security also add
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }
}