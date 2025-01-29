package com.wallboard.wallboard.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;

public class IDNormalizer {
    public static String normalize(String pbxID) {
        if (pbxID == null || pbxID.trim().isEmpty()) {
            throw new IllegalArgumentException("PBX ID cannot be null or empty.");
        }
        String encodedID = URLEncoder.encode(pbxID, StandardCharsets.UTF_8);
        if (encodedID.matches("\\d+")) {
            try {
                if (Integer.parseInt(encodedID) <= 0 || Double.parseDouble(encodedID) <= 0) {
                    throw new InputMismatchException("Invalid ID: " + encodedID);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid PBX ID: " + encodedID);
            }
        } else if (encodedID.matches(".*\\D.*")) {  // Contains non-digit characters
            throw new IllegalArgumentException("PBX ID should only contain numbers, received: " + encodedID);
        }

        return encodedID;
    }
}
