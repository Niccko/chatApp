package com.niccko.Chat.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.niccko.Chat.model.Role;
import com.niccko.Chat.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JWTUtils {
    @Value("${jwt.token.expires}")
    private Long jwtExpireTime;

    @Value("${jwt.refresh.expires}")
    private Long refreshExpireTime;

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    public String generateToken(User user, String issuer, boolean refresh) {
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        String token;
        if (!refresh) {
            token = JWT.create()
                    .withSubject(user.getLogin())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpireTime))
                    .withIssuer(issuer)
                    .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                    .sign(algorithm);
            return token;
        }
        algorithm = Algorithm.HMAC256(refreshSecret.getBytes());
        token = JWT.create()
                .withSubject(user.getLogin())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpireTime))
                .withIssuer(issuer)
                .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .sign(algorithm);
        return token;
    }

    public String generateToken(org.springframework.security.core.userdetails.User user, String issuer, boolean refresh) {
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        String token;
        if (!refresh) {
            token = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpireTime))
                    .withIssuer(issuer)
                    .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .sign(algorithm);
            return token;
        }
        algorithm = Algorithm.HMAC256(refreshSecret.getBytes());
        token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpireTime))
                .withIssuer(issuer)
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        return token;

    }

    public DecodedJWT verify(String token) {
        if (token != null) {
            Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        }
        return null;
    }

    public DecodedJWT verifyRefresh(String token) {
        Algorithm algorithm = Algorithm.HMAC256(refreshSecret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);

    }
}
