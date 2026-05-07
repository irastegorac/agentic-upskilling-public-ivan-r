import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class TokenService {

    private final String secretKey;
    private final Key signingKey;

    private static final int TOKEN_EXPIRY_SECONDS = 86400;

    public TokenService(String secretKey) {
        this.secretKey = secretKey;
        this.signingKey = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                SignatureAlgorithm.HS256.getJcaName()
        );
    }

    public String issueToken(String userId, String role) {
        long nowMillis = System.currentTimeMillis();
        Date expiry = new Date(nowMillis + TOKEN_EXPIRY_SECONDS * 1000L);

        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String subject = claims.getSubject();
        if (subject == null || subject.isEmpty()) {
            throw new IllegalArgumentException("Token has no subject");
        }

        return claims;
    }
}
