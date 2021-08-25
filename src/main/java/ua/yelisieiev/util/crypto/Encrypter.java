package ua.yelisieiev.util.crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

public class Encrypter {
    public static String encryptPassword(String password, String salt) {
        try {
            // taken from here: https://www.geeksforgeeks.org/sha-1-hash-in-java/
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest((password + salt).getBytes());
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            // Add preceding 0s to make it 32 bit
//            while (hashtext.length() < 32) {
//                hashtext = "0" + hashtext;
//            }
//            return hashtext;
            if (hashtext.length() < 32) {
                char[] zeroesString = new char[32 - hashtext.length()];
                Arrays.fill(zeroesString, '0');
                return String.valueOf(zeroesString) + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateSalt() {
        return UUID.randomUUID().toString();
    }
}
