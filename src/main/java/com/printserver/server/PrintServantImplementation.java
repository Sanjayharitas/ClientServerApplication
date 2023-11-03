package com.printserver.server;

import com.printserver.interfaces.PrintService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;

import java.security.Key;

public class PrintServantImplementation extends UnicastRemoteObject implements PrintService {
    private static final Logger LOGGER = LogManager.getLogger(PrintServantImplementation.class);
    private Map<String, List<String>> printQueues;
    private Map<String, String> configParameters;
    private boolean isRunning = true;
    public static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public PrintServantImplementation() throws RemoteException {
//        super();
        this.printQueues = new HashMap<>();
        this.configParameters = new HashMap<>();
    }

    @Override
    public String print(String fileName, String printer, String token) throws RemoteException {
        if (validateToken(token, key)) {
            List<String> printerQueue = printQueues.get(printer);
            if (printerQueue == null) {
                printerQueue = new ArrayList<>();
                printQueues.put(printer, printerQueue);
            }
            printerQueue.add(fileName);
            System.out.println(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Print job received: File [" + fileName + "] on Printer [" + printer + "]");
            LOGGER.info(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Print job received: File [" + fileName + "] on Printer [" + printer + "]");
            return "Success.";
        } else {
            LOGGER.error("Token validation failed!!");
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String printQueue(String printer, String token) throws RemoteException {
        if (validateToken(token, key)) {
            List<String> printerQueue = printQueues.get(printer);
            if (printerQueue != null) {
                System.out.println(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Printing jobs for printer [" + printer + "] are:");
                StringBuilder queueInfo = new StringBuilder();
                for (int i = 0; i < printerQueue.size(); i++) {
                    queueInfo.append((i + 1)).append(". ").append(printerQueue.get(i)).append("\n");
                }
                return queueInfo.toString();
            } else {
                return getUsernameFromJWT(token, key) + " - Printer not found in the queue.";
            }
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String topQueue(String printer, int job, String token) throws RemoteException {
        if (validateToken(token, key)) {
            List<String> printerQueue = printQueues.get(printer);

            if (printerQueue != null && job >= 1 && job <= printerQueue.size()) {
                String jobToMove = printerQueue.remove(job - 1);
                printerQueue.add(0, jobToMove);
                return "Success";
            } else {
                LOGGER.error(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Error: Invalid job or printer not found.");
                System.out.println(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Error: Invalid job or printer not found.");
                return "Error!. Invalid job or printer not found.";
            }
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String start(String token) throws RemoteException {
        if (validateToken(token, key)) {
            if (getUsernameFromJWT(token, key).equals("admin")) {
                if (isRunning) {
                    LOGGER.info(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Print server is already running!");
                    System.out.println(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Print server is already running!");
                } else {
                    isRunning = true;
                    LOGGER.info(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Print server is now running.");
                    System.out.println(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Print server is now running.");
                }
                return "Success";
            } else {
                LOGGER.warn("Unauthorized access");
                return "Access Denied!";
            }
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String stop(String token) throws RemoteException {
        if (validateToken(token, key)) {
            if (getUsernameFromJWT(token, key).equals("admin")) {
                if (!isRunning) {
                    LOGGER.info(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Print server is already stopped!");
                    System.out.println(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Print server is already stopped!");
                } else {
                    isRunning = false;
                    LOGGER.info(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Print server is now stopped.");
                    System.out.println(java.time.LocalDateTime.now() + "    " + getUsernameFromJWT(token, key) + " - Print server is now stopped.");
                }
                return "Success";
            } else {
                LOGGER.warn("Unauthorized access");
                return "Access Denied!";
            }
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String restart(String token) throws RemoteException {
        if (validateToken(token, key)) {
            if (getUsernameFromJWT(token, key).equals("admin")) {
                if (isRunning) {
                    stop(token);
                    printQueues.clear();
                    start(token);
                } else {
                    printQueues.clear();
                    start(token);
                }
                return "Success";
            } else {
                LOGGER.warn("Unauthorized access");
                return "Access Denied!";
            }
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String status(String printer, String token) throws RemoteException {
        if (validateToken(token, key)) {
            List<String> printerQueue = printQueues.get(printer);

            if (printerQueue != null) {
                int jobCount = printerQueue.size();
                return getUsernameFromJWT(token, key) + " - " + jobCount + " Job(s) in the queue for Printer [" + printer + "]";
            } else {
                return getUsernameFromJWT(token, key) + " - Error: Printer [" + printer + "] not found.";
            }
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String readConfig(String parameter, String token) throws RemoteException {
        if (validateToken(token, key)) {
            if (getUsernameFromJWT(token, key).equals("admin")) {
                String value = configParameters.get(parameter);
                return Objects.requireNonNullElseGet(value, () -> getUsernameFromJWT(token, key) + " - Error: Parameter " + parameter + " not found.");
            } else {
                LOGGER.warn("Unauthorized access");
                return "Access Denied!";
            }
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String setConfig(String parameter, String value, String token) throws RemoteException {
        if (validateToken(token, key)) {
            if (getUsernameFromJWT(token, key).equals("admin")) {
                configParameters.put(parameter, value);
            } else {
                LOGGER.warn("Unauthorized access");
                return "Access Denied!";
            }
            return "Success";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String auth(String userName, String password) {
        String dob = DBConnection.getDOB(userName);
        String hashPassword = PasswordProtection.PBKDF2(dob, password);
        boolean res = DBConnection.authUser(userName, hashPassword);
        if (res) {
            String token = generateJWTToken(userName);
            LOGGER.info(java.time.LocalDateTime.now() + "    " + "Token generated for " + getUsernameFromJWT(token, key) + "- token: " + token + "\n");
            System.out.println(java.time.LocalDateTime.now() + "    " + "Token generated for " + getUsernameFromJWT(token, key) + "- token: " + token + "\n");
            return token;
        }
        return null;
    }

    private static String getUsernameFromJWT(String token, Key key) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    private static boolean validateToken(String token, Key key) {
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

    // 600000 == to 10minutes,
    private static String generateJWTToken(String username) {
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

