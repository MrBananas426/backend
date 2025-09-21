package com.example.icebreaker.impl;

import java.security.SecureRandom;

public class Generator {

    private static final SecureRandom random = new SecureRandom();

    public static String generateUserId(int length) {
        StringBuilder sb = new StringBuilder(length);
        // First digit should not be zero
        sb.append(random.nextInt(9) + 1);
        for (int i = 1; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
