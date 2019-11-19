package co.infinum.goldfinger;

import java.util.List;

class StringUtils {

    private StringUtils() {
    }

    static boolean isBlankOrNull(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Join list of strings into single string.
     */
    static String join(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String error : list) {
            builder.append(error);
        }
        return builder.toString();
    }
}
