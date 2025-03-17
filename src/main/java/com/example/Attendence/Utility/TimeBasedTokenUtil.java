package com.example.Attendence.Utility;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class TimeBasedTokenUtil {
    private static final String SECRET_KEY = "ic3sOCvghvZuDRikYTQSjAhZz48BFaNs/jF6qqRvibg="; // Change this to a strong secret key
    private static final long EXPIRATION_TIME = 3600000; // Token expires in 1 hour (3600000 ms)

    // Generate token based on the current timestamp
    public static String generateToken() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = currentTime + EXPIRATION_TIME;

        String tokenData = SECRET_KEY + ":" + expirationTime;
        return encode(tokenData);
    }

    // Validate token by checking its expiration
    public static boolean validateToken(String token) {
        String decodedToken = decode(token);
        if (decodedToken == null) return false;

        String[] parts = decodedToken.split(":");
        if (parts.length != 2) return false;

        long expirationTime;
        try {
            expirationTime = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        return expirationTime > currentTime; // Token is valid if it has not expired
    }

    // Base64 encoding with hashing
    private static String encode(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error encoding token", e);
        }
    }

    // Base64 decoding
    private static String decode(String token) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}
