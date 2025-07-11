package com.company.oop.logistics.utils.parsing;

import java.security.InvalidParameterException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParsingHelpers {
    private static final String INVALID_NUMBER_FIELD_MESSAGE = "Invalid value for %s. Should be a number.";
    private static final String INVALID_BOOLEAN_FIELD_MESSAGE = "Invalid value for %s. Should be one of 'true' or 'false'.";
    private static final String INVALID_DATETIME_FIELD_MESSAGE = "Invalid value for %s. Should be in format 1986-04-25-12:30";

    public static double tryParseDouble(String valueToParse, String parameterName) {
        try {
            return Double.parseDouble(valueToParse);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException(String.format(INVALID_NUMBER_FIELD_MESSAGE, parameterName));
        }
    }

    public static int tryParseInteger(String valueToParse, String parameterName) {
        try {
            return Integer.parseInt(valueToParse);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException(String.format(INVALID_NUMBER_FIELD_MESSAGE, parameterName));
        }
    }

    public static boolean tryParseBoolean(String valueToParse, String parameterName) {
        if (!valueToParse.equalsIgnoreCase("true") &&
                !valueToParse.equalsIgnoreCase("false")) {
            throw new InvalidParameterException(String.format(INVALID_BOOLEAN_FIELD_MESSAGE, parameterName));
        }

        return Boolean.parseBoolean(valueToParse);
    }

    public static LocalDateTime tryParseLocalDateTime(String valueToParse, String parameterName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");

        valueToParse = valueToParse.trim().toLowerCase();

        if (valueToParse.equals("now")) {
            return LocalDateTime.now().plusMinutes(1).withSecond(0).withNano(0);
        }

        if (valueToParse.startsWith("now+")) {
            String suffix = valueToParse.substring(4);
            try {
                if (suffix.endsWith("h")) {
                    int hours = Integer.parseInt(suffix.replace("h", ""));
                    if (hours >= 1 && hours <= 23) {
                        return LocalDateTime.now().plusHours(hours).withSecond(0).withNano(0);
                    }
                } else if (suffix.endsWith("m")) {
                    int minutes = Integer.parseInt(suffix.replace("m", ""));
                    if (minutes >= 1 && minutes <= 60) {
                        return LocalDateTime.now().plusMinutes(minutes).withSecond(0).withNano(0);
                    }
                }
            } catch (NumberFormatException ignored) {
                // fall through to error
            }
            throw new DateTimeException("Invalid format for 'now+...' value. Use 'now+1h' to 'now+23h' or 'now+1m' to 'now+60m'.");
        }

        try {
            return LocalDateTime.parse(valueToParse, formatter);
        } catch (RuntimeException e) {
            throw new DateTimeException(String.format(INVALID_DATETIME_FIELD_MESSAGE, parameterName));
        }
    }

    public static <E extends Enum<E>> E tryParseEnum(String valueToParse, Class<E> type, String errorMessage) {
        try {
            return Enum.valueOf(type, valueToParse.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format(errorMessage, valueToParse));
        }
    }
}
