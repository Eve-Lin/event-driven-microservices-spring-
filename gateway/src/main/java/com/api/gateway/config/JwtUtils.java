package com.api.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class JwtUtils {

    @Value("${jwt.public.key}")
    private String publicKeyPem;

    private final ResourceLoader resourceLoader;

    public JwtUtils(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private PublicKey loadPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        Resource resource = resourceLoader.getResource(publicKeyPem);

        String key = new String(resource.getInputStream().readAllBytes())
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

            PublicKey publicKey =  KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(decoded));
            System.out.println("The final public key is: " + publicKey);
            return publicKey;
    }

    public Claims validateTokenAndGetClaims(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
            System.out.println(">>> Inside validate method: " + this.loadPublicKey());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(loadPublicKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println(">>> Claims: " + claims);

            return claims;

    }
}