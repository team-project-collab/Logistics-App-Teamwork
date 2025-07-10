package com.company.oop.logistics.tests.models;

import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.DeliveryPackageImpl;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.models.enums.PackageStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryPackageImplTest {
    private CustomerContactInfo contact;
    private DeliveryPackageImpl deliveryPackage;
    private LocalDateTime now;
    private ArrayList<Location> locations;

    @BeforeEach
    public void setUp() {
        contact = new CustomerContactInfo(1,
                "Ivan Ivanov",
                "+1234567890",
                "ivan@example.com",
                City.MEL);

        deliveryPackage = new DeliveryPackageImpl(1,
                City.MEL,
                City.ADL,
                40.5,
                contact.getId());

    }


    @Test
    public void constructorDeliveryPackage_Should_SetValuesWithSuccess() {

        Assertions.assertEquals(1, deliveryPackage.getId());
        Assertions.assertEquals(City.MEL, deliveryPackage.getStartLocation());
        Assertions.assertEquals(City.ADL, deliveryPackage.getEndLocation());
        Assertions.assertEquals(40.5, deliveryPackage.getWeightKg());
        Assertions.assertEquals(contact.getId(), deliveryPackage.getCustomerContactInfoId());

    }



}


//@Test
//public void getProducts_Should_ReturnCopyOfTheCollection()