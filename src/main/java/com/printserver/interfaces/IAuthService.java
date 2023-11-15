package com.printserver.interfaces;

import java.security.Key;

public interface IAuthService {
    String auth(String userName, String password);
    String getUsernameFromJWT(String token, Key key);
    boolean validateToken(String token, Key key);
    String generateJWTToken(String username, String role);
}
