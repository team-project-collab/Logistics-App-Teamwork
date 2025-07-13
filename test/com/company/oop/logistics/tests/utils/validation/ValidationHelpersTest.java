package com.company.oop.logistics.tests.utils.validation;

import com.company.oop.logistics.utils.validation.ValidationHelpers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ValidationHelpersTest {
    public static final String VALID_STRING = "Invalid range.";
    public static final String EMPTY_STRING = "";

    public static final String VALID_NAME = "Gosho";
    public static final String INVALID_NAME = "John123";
    public static final String SHORT_STRING = "asd";
    public static final String LONG_STRING = "VeryLongString";

    public static final String VALID_PHONE = "0888123456";
    public static final String INVALID_PHONE = "123ABC!@#";

    public static final String VALID_EMAIL = "asd@abv.bg";
    public static final String INVALID_EMAIL = "invalidemail@com";

    public static final String REGEX_PATERN = "^[a-zA-Z]+$";


    @Test
    void validateIntRange_Should_NotThrow_When_Valid() {
        ValidationHelpers.validateIntRange(7, 5, 10, VALID_STRING);
    }
    @Test
    void validateIntRange_Should_Throw_When_OutsideRange() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(IllegalArgumentException.class, () ->
                        ValidationHelpers.validateIntRange(4, 5, 10, VALID_STRING)),
                () -> Assertions.assertThrows(IllegalArgumentException.class, () ->
                        ValidationHelpers.validateIntRange(11, 5, 10, VALID_STRING))

        );
    }

    @Test
    void validateDecimalRange_Should_NotThrow_When_Valid() {
        ValidationHelpers.validateDecimalRange(5.5, 1.0, 10.0, VALID_STRING);
    }

    @Test
    void validateDecimalRange_Should_Throw_When_OutsideRange() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () ->ValidationHelpers.validateDecimalRange(15, 1.0, 10.0, VALID_STRING)
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> ValidationHelpers.validateDecimalRange(0, 1.0, 10.0, VALID_STRING)
                )
        );
    }

    @Test
    void validateArgumentsCount_Should_NotThrow_When_Valid() {
        List<String> args = List.of(VALID_STRING, INVALID_NAME, VALID_PHONE);
        ValidationHelpers.validateArgumentsCount(args, 3);
    }

    @Test
    void validateArgumentsCount_Should_Throw_When_InsufficientArguments() {
        List<String> args = List.of(VALID_STRING, INVALID_NAME);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ValidationHelpers.validateArgumentsCount(args, 3));
    }

    @Test
    void validatePattern_Should_NotThrow_When_Valid() {
        ValidationHelpers.validatePattern(VALID_NAME, REGEX_PATERN, VALID_STRING);
    }

    @Test
    void validatePattern_Should_Throw_When_ValueDoesNotMatchPattern() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ValidationHelpers.validatePattern(INVALID_NAME, REGEX_PATERN, VALID_STRING));
    }

    @Test
    void validateStringLength_Should_NotThrow_When_Valid() {
        ValidationHelpers.validateStringLength(VALID_NAME, 5, 10, VALID_STRING);
    }

    @Test
    void validateStringLength_Should_Throw_When_LengthOutOfBounds() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(IllegalArgumentException.class, () ->
                        ValidationHelpers.validateStringLength(SHORT_STRING, 5, 10, VALID_STRING)),
                () -> Assertions.assertThrows(IllegalArgumentException.class, () ->
                        ValidationHelpers.validateStringLength(LONG_STRING, 5, 10, VALID_STRING))
        );
    }

    @Test
    void validateTimeAgainstPresent_Should_NotThrow_When_TimeIsInTheFuture() {
        LocalDateTime pastTime = LocalDateTime.now().plusMinutes(2);
        ValidationHelpers.validateTimeAgainstPresent(pastTime, VALID_STRING);
    }

    @Test
    void validateTimeAgainstPresent_Should_Throw_When_TimeIsInThePast() {
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(2);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ValidationHelpers.validateTimeAgainstPresent(pastTime, VALID_STRING));
    }

    @Test
    void validateTimeAgainstTime_Should_NotThrow_When_Valid() {
        LocalDateTime start = LocalDateTime.now().minusMinutes(10);
        LocalDateTime end = LocalDateTime.now();
        ValidationHelpers.validateTimeAgainstTime(start, end, VALID_STRING, VALID_STRING);
    }

    @Test
    void validateTimeAgainstTime_Should_Throw_When_EndTimeBeforeStartTime() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(10);
        LocalDateTime end = LocalDateTime.now();
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ValidationHelpers.validateTimeAgainstTime(start, end, VALID_STRING, VALID_STRING));
    }

    @Test
    void validateUniqueList_Should_NotThrow_When_Valid() {
        List<String> list = List.of(VALID_STRING, INVALID_NAME);
        ValidationHelpers.validateUniqueList(list, VALID_STRING);
    }

    @Test
    void validateUniqueList_Should_Throw_When_ListContainsDuplicates() {
        List<String> list = List.of(VALID_STRING, VALID_STRING, INVALID_NAME);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ValidationHelpers.validateUniqueList(list, VALID_STRING));
    }

    @Test
    void validateName_Should_NotThrow_When_Valid() {
        ValidationHelpers.validateName(VALID_NAME);
    }

    @Test
    void validateName_Should_Throw_When_NameIsEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ValidationHelpers.validateName(EMPTY_STRING));
    }

    @Test
    void validateName_Should_Throw_When_NameIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ValidationHelpers.validateName(null));
    }

    @Test
    void validateName_Should_Throw_When_NameHasInvalidCharacters() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ValidationHelpers.validateName(INVALID_NAME));
    }

    @Test
    void validateEmail_Should_NotThrow_When_Valid(){
        ValidationHelpers.validateEmail(VALID_EMAIL);
    }

    @Test
    void validateEmail_Should_Throw_When_EmailIsInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ValidationHelpers.validateEmail(INVALID_EMAIL));
    }

    @Test
    void validateEmail_Should_Throw_When_EmailIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ValidationHelpers.validateEmail(null));
    }

    @Test
    void validatePhoneNumber_Should_Throw_When_PhoneNumberIsNullOrEmpty() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(IllegalArgumentException.class, () ->
                        ValidationHelpers.validatePhoneNumber(EMPTY_STRING)),
                () -> Assertions.assertThrows(IllegalArgumentException.class, () ->
                        ValidationHelpers.validatePhoneNumber(null))
        );
    }

    @Test
    void validatePhoneNumber_Should_NotThrow_WhenValid() {
        ValidationHelpers.validatePhoneNumber(VALID_PHONE);
    }

    @Test
    void validatePhoneNumber_Should_Throw_When_PhoneNumberIsMalformed() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ValidationHelpers.validatePhoneNumber(INVALID_PHONE));
    }

}
