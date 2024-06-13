package com.office.api.configuration;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
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

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static java.util.Base64.getMimeDecoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final String publicKey = System.getenv("PUBLIC_KEY");
    private final String privateKey = System.getenv("PRIVATE_KEY");

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
                                "/projects/{id}",
                                "/employees").hasAuthority("SCOPE_COMPANY")

                        .requestMatchers(HttpMethod.POST,
                                "/teams",
                                "/tasks").hasAuthority("SCOPE_MANAGER")
                        .requestMatchers(HttpMethod.PUT,
                                "/teams/{id}",
                                "/tasks/{id}").hasAuthority("SCOPE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE,
                                "/teams/{id}",
                                "/tasks/{id}").hasAuthority("SCOPE_MANAGER")
                        .requestMatchers(HttpMethod.GET,
                                "/tasks/{id}",
                                "/comments/{id}",
                                "/teams/project").hasAuthority("SCOPE_MANAGER")

                        .requestMatchers(HttpMethod.POST,
                                "/comments/{id}").hasAuthority("SCOPE_EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT,
                                "/employees",
                                "/comments/{id}").hasAuthority("SCOPE_EMPLOYEE")
                        .requestMatchers(HttpMethod.DELETE,
                                "/comments/{id}").hasAuthority("SCOPE_EMPLOYEE")
                        .requestMatchers(HttpMethod.GET,
                                "/tasks",
                                "/comments",
                                "/employees/me").hasAuthority("SCOPE_EMPLOYEE")

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
    public JwtDecoder decoder() throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(getMimeDecoder().decode(publicKey));
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

        return NimbusJwtDecoder.withPublicKey(pubKey).build();
    }
    @Bean
    public JwtEncoder encoder() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(getMimeDecoder().decode(privateKey));
        PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(getMimeDecoder().decode(publicKey));
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

        JWK jwk = new RSAKey.Builder(pubKey).privateKey(privKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}