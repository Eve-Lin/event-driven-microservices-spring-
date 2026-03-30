package com.security.auth.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.private.key}")
    private String privateKeyPem;

    private final ResourceLoader resourceLoader;

    public JwtUtils(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private PrivateKey loadPrivateKey() {
        try {
            Resource resource = resourceLoader.getResource(privateKeyPem);

            String key = new String(resource.getInputStream().readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);

            return KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String email, String role) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(loadPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }
}