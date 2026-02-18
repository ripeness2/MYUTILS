package org.ripeness.myutils.utils.textutil;

public class stringController {

    public static boolean isInt(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        if (input.equals("00")) {
            return true;
        }
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isString(Object input) {
        return input instanceof String;
    }

    public static boolean isBoolean(String input) {
        if (input == null) {
            return false;
        }
        return input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false");
    }

}
