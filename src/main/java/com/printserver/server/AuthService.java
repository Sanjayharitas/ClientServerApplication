package com.printserver.server;

import com.printserver.interfaces.IAuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.Key;
import java.util.Date;

public class AuthService implements IAuthService {
    private static final Logger LOGGER = LogManager.getLogger(PrintServantImplementation.class);
    public static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // 600000 == to 10minutes,
    @Override
    public String auth(String userName, String password) {
        boolean res = PasswordProtection.verifyPassword(password, userName);
        if (res) {
            String role = DBConnection.getRole(userName);
            String token = generateJWTToken(userName, role);
            LOGGER.info(java.time.LocalDateTime.now() + "    " + "Token generated for " + getUsernameFromJWT(token, key) + "- token: " + token + "\n");
            System.out.println(java.time.LocalDateTime.now() + "    " + "Token generated for " + getUsernameFromJWT(token, key) + "- token: " + token + "\n");
            return token;
        }
        return null;
    }

    @Override
    public String getUsernameFromJWT(String token, Key key) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    @Override
    public boolean validateToken(String token, Key key) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String generateJWTToken(String username, String role) {
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + 600000);

        return Jwts.builder()
                .setSubject(username)

                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
