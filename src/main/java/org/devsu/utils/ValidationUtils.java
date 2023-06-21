package org.devsu.utils;

public class ValidationUtils {

    public static boolean validateDocument(String document) {
        int regionDigit = Integer.parseInt(document.substring(0,2));
        if(regionDigit < 1 || regionDigit > 24) return false;

        char[] charDocument = document.toCharArray();
        int lastDigit   = Integer.parseInt(String.valueOf(charDocument[9]));
        int sum = 0;

        for(int i=0; i<9; i++) {
            int current = Integer.parseInt(String.valueOf(charDocument[i]));
            if (i % 2 == 0) {
                current *= 2;
                if (current > 9) current -= 9;
            }
            sum += current;
        }

        int immediate = sum - (sum % 10);
        immediate += immediate < sum ? 10 : 0;

        return (immediate - sum) == lastDigit;
    }
}
