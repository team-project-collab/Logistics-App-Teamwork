package com.company.oop.logistics.utils.validation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelpers {

    private static final String INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments. Expected: %d; received: %d.";
    public static final String TIME_IN_THE_PAST_ERR = "Time for %s cannot be in the past.";

    public static void validateIntRange(int value, int min, int max, String message) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateDecimalRange(double value, double min, double max, String message) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateArgumentsCount(List<String> list, int expectedNumberOfParameters) {
        if (list.size() < expectedNumberOfParameters) {
            throw new IllegalArgumentException(
                    String.format(INVALID_NUMBER_OF_ARGUMENTS, expectedNumberOfParameters, list.size())
            );
        }
    }

    public static void validatePattern(String value, String pattern, String message) {
        Pattern patternToMatch = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = patternToMatch.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateStringLength(String value, int min, int max, String message) {
        if (value.length() < min || value.length() > max) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateTimeAgainstPresent(LocalDateTime time, String field) {
        if (LocalDateTime.now().isAfter(time)) {
            throw new IllegalArgumentException(String.format(TIME_IN_THE_PAST_ERR, field));
        }
    }

    public static void validateTimeAgainstTime(LocalDateTime timeBefore, LocalDateTime timeAfter, String fieldBefore, String fieldAfter) {
        if (timeBefore.isAfter(timeAfter)) {
            throw new IllegalArgumentException(String.format("Time for %s cannot be after time for %s.", fieldBefore, fieldAfter));
        }
    }
}
