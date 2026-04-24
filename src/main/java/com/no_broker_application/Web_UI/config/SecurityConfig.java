package com.no_broker_application.Web_UI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SecurityConfig {

        @Autowired
        private ClientRegistrationRepository clientRegistrationRepository;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/chatbot",
                                                                "/images/**",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/static/**",
                                                                "/login",
                                                                "/error",
                                                                "/landingPage",
                                                                "/loginPage",
                                                                "/send-otp",
                                                                "/verify",
                                                                "/verify-otp")
                                                .permitAll()
                                                .requestMatchers("/viewProperty/**", "/view-full-property", "/edit/**")
                                                .hasAnyRole("OIDC_USER", "USER")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .permitAll())
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login")
                                                .authorizationEndpoint(authorization -> authorization
                                                                .authorizationRequestResolver(
                                                                                authorizationRequestResolver(
                                                                                                clientRegistrationRepository)))
                                                .defaultSuccessUrl("/saveUser", true)
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userAuthoritiesMapper(authorities -> {
                                                                        Set<GrantedAuthority> mappedAuthorities = new HashSet<>(
                                                                                        authorities);
                                                                        mappedAuthorities
                                                                                        .add(new SimpleGrantedAuthority(
                                                                                                        "ROLE_USER"));
                                                                        return mappedAuthorities;
                                                                })))
                                .logout(logout -> logout
                                                .logoutRequestMatcher(getLogoutRequestMatcher())
                                                .deleteCookies("jwt_token", "JSESSIONID", "SESSION")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .logoutSuccessUrl("/landingPage")
                                                .permitAll());
                return http.build();
        }

        private RequestMatcher getLogoutRequestMatcher() {
                return request -> "GET".equalsIgnoreCase(request.getMethod())
                                && "/logout".equals(request.getRequestURI());
        }

        private OAuth2AuthorizationRequestResolver authorizationRequestResolver(
                        ClientRegistrationRepository clientRegistrationRepository) {
                DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
                                clientRegistrationRepository, "/oauth2/authorization");
                authorizationRequestResolver.setAuthorizationRequestCustomizer(
                                customizer -> customizer.additionalParameters(
                                                params -> params.put("prompt", "select_account")));
                return authorizationRequestResolver;
        }
}
