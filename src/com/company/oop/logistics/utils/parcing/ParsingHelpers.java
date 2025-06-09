package com.company.oop.logistics.utils.parcing;

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

    public static LocalDateTime tryParseLocalDateTime(String valueToParse, String parameterName){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
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
