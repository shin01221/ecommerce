package com.ecommerce.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashGenerator {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("BCrypt hash for 'admin123': " + hash);
        System.out.println("Verify: " + BCrypt.checkpw(password, hash));
    }
}
