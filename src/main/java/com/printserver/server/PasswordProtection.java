package com.printserver.server;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Takes care of hashing
 */
public class PasswordProtection {
    private static final Logger LOGGER = LogManager.getLogger(PasswordProtection.class);

    //   To generate the hashed password during user enrollment
    public static String hashPassword(String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return hashedPassword;
    }

    public static boolean verifyPassword(String passwordToVerify, String user) {
        String passwordFromDB = DBConnection.getPW(user);
        if (passwordFromDB != null && BCrypt.checkpw(passwordToVerify, passwordFromDB)) {
            LOGGER.info("Authentication for " + user + " successful.");
            System.out.println("Password matches!");
            return true;
        } else {
            LOGGER.info("Authentication for " + user + " failed.");
            System.out.println("Password does not match.");
            return false;
        }
    }


//    public static void main(String[] args) {
//        System.out.println(verifyPassword("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8","$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46","admin"));
//    }

//    The below implementation is for PBKDF2

//    public static String PBKDF2(String dob, String password) {
//
//        String salt = dob;
//        int iterations = 10000;
//        int keyLength = 512;
//        char[] passwordChars = password.toCharArray();
//        byte[] saltBytes = salt.getBytes();
//
//        byte[] hashedBytes = hashPassword(passwordChars, saltBytes, iterations, keyLength);
//        String hashedString = Hex.encodeHexString(hashedBytes);
////        For testing purpose
//        System.out.println(hashedString);
//        return hashedString;
//    }

//    public static byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {
//
//        try {
//            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
//            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
//            SecretKey key = skf.generateSecret(spec);
//            byte[] res = key.getEncoded();
//            return res;
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//            throw new RuntimeException(e);
//        }
//    }

}