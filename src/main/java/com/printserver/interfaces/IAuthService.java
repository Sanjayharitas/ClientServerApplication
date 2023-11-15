package com.printserver.interfaces;

import java.security.Key;

public interface IAuthService {
    String auth(String userName, String password);
    String getUsernameFromJWT(String token);
    boolean validateToken(String token);
    String generateJWTToken(String username);
    String getRole(String userName);
    String getRoleFromToken(String token);
}
