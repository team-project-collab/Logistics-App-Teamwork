package com.company.oop.logistics.tests.models;

import com.company.oop.logistics.models.LocationImpl;
import com.company.oop.logistics.models.enums.City;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class LocationImplTest {
    @Test
    public void constructorLocationImpl_Should_setValuesWithSuccess() {

        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);
        LocationImpl location = new LocationImpl(1, City.ADL, arrivalTime, departureTime);

        Assertions.assertEquals(1, location.getId());
        Assertions.assertEquals(City.ADL, location.getName());
        Assertions.assertEquals(arrivalTime, location.getArrivalTime());
        Assertions.assertEquals(departureTime, location.getDepartureTime());
    }

    @Test
    public void constructorLocationImpl_Should_ThrowException_When_ArrivalTimeIsInThePast() {
        LocalDateTime arrivalTime = LocalDateTime.now().minusHours(1);
        LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LocationImpl(1, City.ADL, arrivalTime, departureTime));
    }

    @Test
    public void constructorLocationImpl_Should_ThrowException_When_DepartureTimeIsInThePast() {
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);
        LocalDateTime departureTime = LocalDateTime.now().minusHours(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LocationImpl(1, City.ADL, arrivalTime, departureTime));
    }

    @Test
    public void constructorLocationImpl_Should_ThrowException_When_DepartureTimeIsBeforeArrivalTime() {
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(2);
        LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LocationImpl(1, City.ADL, arrivalTime, departureTime));
    }

    @Test
    public void setDepartureTime_Should_ThrowException_When_DepartureTimeIsBeforeArrivalTime() {
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);

        LocationImpl location = new LocationImpl(1, City.MEL, arrivalTime, departureTime);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> location.setDepartureTime(LocalDateTime.now().plusMinutes(30)));

    }

}


