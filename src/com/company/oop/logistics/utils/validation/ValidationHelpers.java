package com.company.oop.logistics.utils.validation;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelpers {

    private static final String INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments. Expected: %d; received: %d.";
    private static final String TIME_IN_THE_PAST_ERR = "Time for %s cannot be in the past.";
    public static final String REGEX_NAME = "^[A-Za-z][A-Za-z\\s'-]*$";
    public static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final int MINUTES_TOLERANCE = 1;
    public static final String ERROR_EMAIL_FORMAT = "Invalid email address provided: ";
    public static final String ERROR_NAME_LENGTH = "Full name cannot be null or empty.";
    public static final String ERROR_NAME_FORMAT = "Invalid full name format.";
    public static final String REGEX_PHONE = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]{6,}$";
    public static final String ERROR_PHONE_NUMBER_LENGTH = "Phone number cannot be null or empty.";
    public static final String ERROR_PHONE_NUMBER_FORMAT = "Invalid phone number format.";
    public static final String ERROR_TIME_BEFORE = "Time for %s cannot be after time for %s.";

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
        if (time.isBefore(LocalDateTime.now().minusMinutes(MINUTES_TOLERANCE))) {
            throw new IllegalArgumentException(String.format(TIME_IN_THE_PAST_ERR, field));
        }
    }

    public static void validateTimeAgainstTime(LocalDateTime timeBefore, LocalDateTime timeAfter, String fieldBefore, String fieldAfter) {
        if (!timeAfter.isAfter(timeBefore)) {
            throw new IllegalArgumentException(String.format(ERROR_TIME_BEFORE, fieldBefore, fieldAfter));
        }
    }

    public static <T> void validateUniqueList(List<T> list, String message) {
        Set<T> uniqueSet = new HashSet<>();
        for (T item : list) {
            if (!uniqueSet.add(item)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    public static void validateName(String fullName){
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_NAME_LENGTH);
        }
        if (!fullName.matches(REGEX_NAME)) {
            throw new IllegalArgumentException(ERROR_NAME_FORMAT);
        }
    }

    public static void validateEmail(String email){
        if (email == null || !email.matches(REGEX_EMAIL)) {
            throw new IllegalArgumentException(ERROR_EMAIL_FORMAT + email);
        }
    }

    public static void validatePhoneNumber(String phoneNumber){
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_PHONE_NUMBER_LENGTH);
        }
        if (!phoneNumber.matches(REGEX_PHONE)) {
            throw new IllegalArgumentException(ERROR_PHONE_NUMBER_FORMAT);
        }
    }
}
