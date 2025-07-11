package com.company.oop.logistics.tests.models;

import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.DeliveryPackageImpl;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryPackageImplTest {

    private DeliveryPackageImpl deliveryPackage;

    @BeforeEach
    public void setUp() {
        deliveryPackage = new DeliveryPackageImpl(1,
                City.MEL,
                City.ADL,
                40.5,
                10);
    }


    @Test
    public void constructorDeliveryPackage_Should_SetValuesWithSuccess() {

        Assertions.assertEquals(1, deliveryPackage.getId());
        Assertions.assertEquals(City.MEL, deliveryPackage.getStartLocation());
        Assertions.assertEquals(City.ADL, deliveryPackage.getEndLocation());
        Assertions.assertEquals(40.5, deliveryPackage.getWeightKg());
        Assertions.assertEquals(10, deliveryPackage.getCustomerContactInfoId());
    }

    @Test
    public void setLocations_Should_UpdateLocationsSuccessfully() {
        ArrayList<Integer> locations = new ArrayList<>();
        locations.add(1);
        locations.add(2);
        deliveryPackage.setLocations(locations);
        Assertions.assertEquals(locations, deliveryPackage.getLocations());
    }

    @Test
    public void assign_Should_SetAssignedSuccessfully() {
        deliveryPackage.assign(5);
        Assertions.assertTrue(deliveryPackage.isAssigned());
        Assertions.assertEquals(5, deliveryPackage.getAssignedRoute());
    }

    @Test
    public void toString_Should_ReturnFormattedString() {
        String expectedOutput = "Package id: 1; Origin: MEL; Destination: ADL;";
        Assertions.assertEquals(expectedOutput, deliveryPackage.toString());
    }

}


