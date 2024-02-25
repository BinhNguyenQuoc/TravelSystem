package com.hypothetical.travel.HypotheticalTravelSystem.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Utilities {

    public static String hashingPAN(String PANNumber) {
        if (isValidCreditCard(PANNumber)) {
            return DigestUtils.md5DigestAsHex(PANNumber.getBytes());
        }
        return null;
    }

    public static boolean isValidCreditCard(String PANNumber) {
        if (PANNumber == null || PANNumber.length() < 13 || PANNumber.length() > 19) {
            return false;
        }

        String cleanedCardNumber = PANNumber.replaceAll("[^\\d]", "");

        int sum = 0;
        boolean alternate = false;
        for (int i = cleanedCardNumber.length() - 1; i >= 0; i--) {
            int digit = Integer.parseInt(cleanedCardNumber.substring(i, i + 1));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            sum += digit;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}
