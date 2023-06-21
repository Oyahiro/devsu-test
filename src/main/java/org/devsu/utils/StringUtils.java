package org.devsu.utils;

import java.util.Objects;

public class StringUtils {

    public static String trimToNull(String string) {
        if (Objects.isNull(string)) {
            return null;
        }
        final String trimmedString = string.trim();
        return trimmedString.isEmpty() ? null : trimmedString;
    }

    public static String generateUsername(String names, String surnames, int additional) {
        String[] namesCollection = names.trim().split("\\s+");
        String[] surnamesCollection = surnames.trim().split("\\s+");

        return String.valueOf(namesCollection[0].charAt(0))
                .concat(
                        namesCollection.length > 1 ? String.valueOf(namesCollection[1].charAt(0)) : ""
                )
                .concat(surnamesCollection[0])
                .concat(
                        additional > 0 ? String.valueOf(additional) : ""
                ).toLowerCase();
    }

}
