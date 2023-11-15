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
            String token = generateJWTToken(userName);
            LOGGER.info(java.time.LocalDateTime.now() + "    " + "Token generated for " + getUsernameFromJWT(token) + "- token: " + token + "\n");
            System.out.println(java.time.LocalDateTime.now() + "    " + "Token generated for " + getUsernameFromJWT(token) + "- token: " + token + "\n");
            return token;
        }
        return null;
    }

    @Override
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    @Override
    public boolean validateToken(String token) {
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
    public String generateJWTToken(String username) {
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + 600000);
        String role = getRole(username);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .claim("role", role)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public String getRole(String userName){
        return DBConnection.getRole(userName);
    }

    @Override
    public String getRoleFromToken(String token) {
        if(validateToken(token)){
            Claims claims =  Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            return claims.get("role", String.class);
        }
        return null;
    }
}
