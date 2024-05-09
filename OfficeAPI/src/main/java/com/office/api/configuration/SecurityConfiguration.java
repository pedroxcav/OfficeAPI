package com.office.api.configuration;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;
    @Value("${jwt.private.key}")
    private RSAPrivateKey privateKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,
                                "/employees",
                                "/projects").hasAuthority("SCOPE_COMPANY")
                        .requestMatchers(HttpMethod.PUT,
                                "/adresses",
                                "/companies",
                                "/projects/{id}").hasAuthority("SCOPE_COMPANY")
                        .requestMatchers(HttpMethod.DELETE,
                                "/companies",
                                "/employees/{username}",
                                "/projects/{id}").hasAuthority("SCOPE_COMPANY")
                        .requestMatchers(HttpMethod.GET,
                                "/teams",
                                "/adresses",
                                "/projects",
                                "/companies",
                                "projects/{id}",
                                "/employees/{username}",
                                "/employees").hasAuthority("SCOPE_COMPANY")
                        .requestMatchers(HttpMethod.PUT, "/employees").hasAuthority("SCOPE_EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/employees/me").hasAuthority("SCOPE_EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/teams").hasAuthority("SCOPE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/teams/{id}").hasAuthority("SCOPE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/teams/{id}").hasAuthority("SCOPE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/teams/project").hasAuthority("SCOPE_MANAGER")
                        .requestMatchers(HttpMethod.POST,
                                "/companies",
                                "/companies/login",
                                "/employees/login").permitAll()
                        .anyRequest().authenticated())
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public JwtEncoder encoder() {
        JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
    @Bean
    public JwtDecoder decoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}