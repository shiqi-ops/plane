package shiqifu.plane.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 获取密钥对象
     */
    private SecretKey getSecretKey() {
        byte[] encodedKey = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(encodedKey);
    }
    /**
        普通的    */
    private SecretKey getSecretKeyFromString() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    /**
     * 从 Token 中提取用户名 (或其他 claim)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    /**
     * 提取特定 Claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> function) {
        final Claims claims=extractAllClaims(token);
        return function.apply(claims);
    }
    /**
     * 生成 Token
     * @param username 用户标识 (通常存入 subject)
     * @param extraClaims 额外信息 (如 role, id 等)
     */
    public String generateToken(String username, Map<String, Object> extraClaims){
        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSecretKeyFromString())
                .compact();
    }
    /**
        简单方法    */
    public String generateToken(String username){
        return generateToken(username, new HashMap<>());
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    /**
     * 解析所有 Claims (核心解析逻辑)
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKeyFromString())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
