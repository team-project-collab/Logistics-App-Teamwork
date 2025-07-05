package com.company.oop.logistics.tests.models;

import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerContactInfoTest {

    @Test
    void constructorCustomerContactInfo_Should_SetValuesWithSuccess() {

        CustomerContactInfo customerContactInfo = new CustomerContactInfo(1,
                "Ivan Ivanov",
                "+1234567890",
                "ivan@example.com",
                City.MEL);
        Assertions.assertEquals("Ivan Ivanov", customerContactInfo.getFullName());
        Assertions.assertEquals("+1234567890", customerContactInfo.getPhoneNumber());
        Assertions.assertEquals("ivan@example.com", customerContactInfo.getEmail());
        Assertions.assertEquals(City.MEL, customerContactInfo.getAddress());
    }

    @Test
    void invalidFullName_Should_ThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CustomerContactInfo(1,
                        "",
                        "+1234567890",
                        "ivan@example.com",
                        City.MEL));
    }

    @Test
    void invalidPhoneNumber_Should_ThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CustomerContactInfo(1,
                        "Ivan Ivanov",
                        "abc",
                        "ivan@example.com",
                        City.MEL));
    }

    @Test
    void invalidEmail_Should_ThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerContactInfo(1,
                        "Ivan Ivanov",
                        "+1234567890",
                        "invalid-email",
                        City.MEL)
        );
    }

    @Test
    void nullCity_Should_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerContactInfo(1,
                        "John Doe",
                        "+1234567890",
                        "john@example.com",
                        null)
        );
    }

}
