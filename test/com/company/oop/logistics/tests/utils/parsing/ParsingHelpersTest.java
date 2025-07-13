package com.company.oop.logistics.tests.utils.parsing;

import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParsingHelpersTest {
    private final static String VALID_STRING = "String";
    private final static String VALID_DOUBLE = "2.2";
    private final static String VALID_INT = "2";
    private final static String VALID_TRUE = "true";
    private final static String VALID_FALSE = "false";
    private final static String NOW_KEYWORD = "now";
    private final static String NOW_PLUS_KEYWORD = "now+5h";
    private final static DateTimeFormatter CORRECT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
    private final static DateTimeFormatter FALSE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final String VALID_ENUM = "syd";
    public static final String FALSE_ENUM = "SOF";


    @Test
    public void tryParseDouble_Should_ReturnParsedDouble_When_ValidDoubleProvided() {
        Assertions.assertEquals(
                Double.parseDouble(VALID_DOUBLE),
                ParsingHelpers.tryParseDouble(VALID_DOUBLE, VALID_STRING)
        );
    }

    @Test
    public void tryParseDouble_Should_ThrowException_When_InvalidDoubleProvided() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ParsingHelpers.tryParseDouble(VALID_STRING, VALID_STRING)
        );
    }

    @Test
    public void tryParseInteger_Should_ReturnParsedInteger_When_ValidIntegerProvided() {
        Assertions.assertEquals(
                Integer.parseInt(VALID_INT),
                ParsingHelpers.tryParseInteger(VALID_INT, VALID_STRING)
        );
    }

    @Test
    public void tryParseInteger_Should_ThrowException_When_InvalidIntegerProvided() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ParsingHelpers.tryParseInteger(VALID_STRING, VALID_STRING)
        );
    }

    @Test
    public void tryParseBoolean_Should_ReturnTrue_When_TrueStringProvided() {
        Assertions.assertTrue(ParsingHelpers.tryParseBoolean(VALID_TRUE, VALID_STRING));
    }

    @Test
    public void tryParseBoolean_Should_ReturnFalse_When_FalseStringProvided() {
        Assertions.assertFalse(ParsingHelpers.tryParseBoolean(VALID_FALSE, VALID_STRING));
    }

    @Test
    public void tryParseBoolean_Should_ThrowException_When_InvalidBooleanProvided() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ParsingHelpers.tryParseBoolean(VALID_STRING, VALID_STRING)
        );}

    @Test
    public void tryParseLocalDateTime_Should_ReturnNowPlusOneMinute_When_NowProvided() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime generated = ParsingHelpers.tryParseLocalDateTime(NOW_KEYWORD, VALID_STRING);
        Assertions.assertAll(
                () -> Assertions.assertTrue(now.isBefore(generated)),
                () -> Assertions.assertTrue(now.plusMinutes(2).isAfter(generated))
        );
    }

    @Test
    public void tryParseLocalDateTime_Should_ReturnFutureDateTime_When_NowPlusHoursProvided() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime generated = ParsingHelpers.tryParseLocalDateTime(NOW_KEYWORD + "+5h", VALID_STRING);
        Assertions.assertTrue(now.plusMinutes(5).isBefore(generated));
    }

    @Test
    public void tryParseLocalDateTime_Should_ReturnFutureDateTime_When_NowPlusMinutesProvided() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime generated = ParsingHelpers.tryParseLocalDateTime(NOW_KEYWORD + "+15m", VALID_STRING);
        Assertions.assertTrue(now.plusMinutes(5).isBefore(generated));
    }

    @Test
    public void tryParseLocalDateTime_Should_ThrowException_When_InvalidNowPlusFormatProvided() {
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        DateTimeException.class,
                        () -> ParsingHelpers.tryParseLocalDateTime(NOW_KEYWORD + "+25h", VALID_STRING)
                ),
                () -> Assertions.assertThrows(
                        DateTimeException.class,
                        () -> ParsingHelpers.tryParseLocalDateTime(NOW_KEYWORD + "+0h", VALID_STRING)
                ),
                () -> Assertions.assertThrows(
                        DateTimeException.class,
                        () -> ParsingHelpers.tryParseLocalDateTime(NOW_KEYWORD + "+61m", VALID_STRING)
                ),
                () -> Assertions.assertThrows(
                        DateTimeException.class,
                        () -> ParsingHelpers.tryParseLocalDateTime(NOW_KEYWORD + "+0m", VALID_STRING)
                ),
                () -> Assertions.assertThrows(
                        DateTimeException.class,
                        () -> ParsingHelpers.tryParseLocalDateTime(NOW_KEYWORD + "+5s", VALID_STRING)
                )
        );
    }

    @Test
    public void tryParseLocalDateTime_Should_ReturnParsedDateTime_When_ValidFormattedDateTimeProvided() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime generated = ParsingHelpers.tryParseLocalDateTime(now.format(CORRECT_FORMAT), VALID_STRING);
        Assertions.assertEquals(
                now.withSecond(0).withNano(0),
                generated
        );
    }

    @Test
    public void tryParseLocalDateTime_Should_ThrowException_When_InvalidFormattedDateTimeProvided() {
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertThrows(
            DateTimeException.class,
            () -> ParsingHelpers.tryParseLocalDateTime(now.format(FALSE_FORMAT), VALID_STRING)
        );
    }

    @Test
    public void tryParseEnum_Should_ReturnEnumConstant_When_ValidEnumNameProvided() {
        City city = ParsingHelpers.tryParseEnum(VALID_ENUM, City.class, VALID_STRING);
        Assertions.assertEquals(City.SYD, city);
    }

    @Test
    public void tryParseEnum_Should_ThrowException_When_InvalidEnumNameProvided() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ParsingHelpers.tryParseEnum(FALSE_ENUM, City.class, VALID_STRING)
        );
    }





}
