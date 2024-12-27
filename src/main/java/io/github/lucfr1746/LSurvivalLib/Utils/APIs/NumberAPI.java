package io.github.lucfr1746.LSurvivalLib.Utils.APIs;

import java.text.DecimalFormat;

public class NumberAPI {

    public static String toString(long number) {
        return String.valueOf(number);
    }

    public static String toString(double number) {
        return String.valueOf(number);
    }

    public static String toString(float number) {
        return String.valueOf(number);
    }

    public static String toString(int number) {
        return String.valueOf(number);
    }

    public static String toString(short number) {
        return String.valueOf(number);
    }

    public static String toString(byte number) {
        return String.valueOf(number);
    }

    public static boolean isNumber(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveNumber(String input) {
        try {
            double num = Double.parseDouble(input);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String input) {
        try {
            int num = Integer.parseInt(input);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String toStringFixed(double number) {
        return toStringFixed(number, 0);
    }

    public static String toStringFixed(double number, int takeBehindComma) {
        StringBuilder formatter = new StringBuilder("#,###,###,###,###,###,###");
        if (takeBehindComma > 0) {
            formatter.append(".");
            formatter.append("#".repeat(takeBehindComma));
        }
        DecimalFormat df = new DecimalFormat(formatter.toString());
        return df.format(number);
    }
}
