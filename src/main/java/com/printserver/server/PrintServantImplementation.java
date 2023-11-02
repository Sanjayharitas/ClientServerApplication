package com.printserver.server;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;

public class PrintServantImplementation extends UnicastRemoteObject implements PrintService {
    private Map<String, List<String>> printQueues;
    private Map<String, String> configParameters;
    private boolean isRunning = true;
    private String sessionIdGenerated;
    private Key _key;
    public PrintServantImplementation() throws RemoteException {
//        super();
        this.printQueues = new HashMap<>();
        this.configParameters = new HashMap<>();
    }

    @Override
    public void print(String fileName, String printer, String token, Key key) throws RemoteException {
        if (validateToken(token, key)) {
            List<String> printerQueue = printQueues.get(printer);
            if (printerQueue == null) {
                printerQueue = new ArrayList<>();
                printQueues.put(printer, printerQueue);
            }
            printerQueue.add(fileName);

            System.out.println(getUsernameFromJWT(token, key) + " - Print job received: File [" + fileName + "] on Printer [" + printer + "]");
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String printQueue(String printer, String token, Key key) throws RemoteException {
        if (validateToken(token, key)) {
            List<String> printerQueue = printQueues.get(printer);
            if (printerQueue != null) {
                System.out.println(" - Printing jobs for printer [" + printer + "] are:");
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
    public void topQueue(String printer, int job, String token, Key key) throws RemoteException {
        if (validateToken(token, key)) {
            List<String> printerQueue = printQueues.get(printer);

            if (printerQueue != null && job >= 1 && job <= printerQueue.size()) {
                String jobToMove = printerQueue.remove(job - 1);
                printerQueue.add(0, jobToMove);
            } else {
                System.out.println(getUsernameFromJWT(token, key) + " - Error: Invalid job or printer not found.");
            }
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public void start(String token, Key key) throws RemoteException {
        if (validateToken(token, key)) {
            if (isRunning) {
                System.out.println(getUsernameFromJWT(token, key) + " - Print server is already running!");
            } else {
                isRunning = true;
                System.out.println(getUsernameFromJWT(token, key) + " - Print server is now running.");
            }
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public void stop(String token, Key key) throws RemoteException {
        if (validateToken(token, key)) {
            if (!isRunning) {
                System.out.println(getUsernameFromJWT(token, key) + " - Print server is already stopped!");
            } else {
                isRunning = false;
                System.out.println(getUsernameFromJWT(token, key) + " - Print server is now stopped.");
            }
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public void restart(String token, Key key) throws RemoteException {
        if (validateToken(token, key)) {
            if (isRunning) {
                stop(token, key);
                printQueues.clear();
                start(token, key);
            } else {
                printQueues.clear();
                start(token, key);
            }
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String status(String printer, String token, Key key) throws RemoteException {
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
    public String readConfig(String parameter, String token, Key key) throws RemoteException {
        if (validateToken(token, key)) {
            String value = configParameters.get(parameter);
            return Objects.requireNonNullElseGet(value, () -> getUsernameFromJWT(token, key) + " - Error: Parameter " + parameter + " not found.");
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public void setConfig(String parameter, String value, String token, Key key) throws RemoteException {
        if (validateToken(token, key)) {
            configParameters.put(parameter, value);
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String auth(String userName, String password) {
        String hashPassword = PasswordProtection.PBKDF2(userName, password);
        boolean res = DBConnection.authUser(userName, hashPassword);
        if (res) {
            byte[] array = new byte[7];
            new Random().nextBytes(array);
            sessionIdGenerated = new String(array, Charset.forName("UTF-8"));
            return sessionIdGenerated;
        }
        return null;
    }
    private static String getUsernameFromJWT(String token, Key key){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    private static boolean validateToken(String token, Key key){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex){
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
}

