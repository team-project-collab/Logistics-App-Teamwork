package com.company.oop.logistics.tests.services;

import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.LocationServiceImpl;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.time.LocalDateTime;
import java.util.List;

public class LocationServiceTest {
    private PersistenceManager persistenceManager;
    private LocationService locationService;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        persistenceManager = new MockPersistenceManagerImpl();
        locationService = new LocationServiceImpl(persistenceManager);
    }

    @Test
    public void createLocation_Should_CreateLocationWithCorrectValues() {
        LocalDateTime arrivalTime = now.plusHours(1);
        LocalDateTime departureTime = now.plusHours(2);

        Location location = locationService.createLocation(City.ADL, arrivalTime, departureTime);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(location),
                () -> Assertions.assertEquals(City.ADL, location.getName()),
                () -> Assertions.assertEquals(arrivalTime, location.getArrivalTime()),
                () -> Assertions.assertEquals(departureTime, location.getDepartureTime())
        );
    }

    @Test
    public void getAllLocations_Should_ReturnAllPersistedLocations() {
        Location location1 = locationService.createLocation(City.MEL, now.plusHours(1), now.plusHours(2));
        Location location2 = locationService.createLocation(City.SYD, now.plusHours(2), now.plusHours(3));

        List<Location> locations = locationService.getAllLocations();

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, locations.size()),
                () -> Assertions.assertTrue(locations.contains(location1)),
                () -> Assertions.assertTrue(locations.contains(location2))
        );
    }

    @Test
    public void getLocationById_Should_ReturnCorrectLocation() {
        Location location = locationService.createLocation(City.PER, now.plusHours(1), now.plusHours(2));
        Location fetched = locationService.getLocationById(location.getId());

        Assertions.assertEquals(location, fetched);
    }

    @Test
    public void getLocationById_Should_ThrowException_WhenNotFound() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> locationService.getLocationById(999)
        );
    }

    @Test
    public void trimLocations_Should_TrimFirstArrivalAndLastDepartureTimes() {
        Location loc1 = locationService.createLocation(City.ADL, now, now.plusHours(1));
        Location loc2 = locationService.createLocation(City.MEL, now.plusHours(2), now.plusHours(3));
        Location loc3 = locationService.createLocation(City.PER, now.plusHours(4), now.plusHours(5));

        List<Location> trimmed = locationService.trimLocations(List.of(loc1, loc2, loc3));

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, trimmed.size()),
                () -> Assertions.assertNull(trimmed.get(0).getArrivalTime()),
                () -> Assertions.assertNull(trimmed.get(2).getDepartureTime()),
                () -> Assertions.assertEquals(loc2.getArrivalTime(), trimmed.get(1).getArrivalTime()),
                () -> Assertions.assertEquals(loc2.getDepartureTime(), trimmed.get(1).getDepartureTime())
        );
    }

    @Test
    public void trimLocations_Should_HandleSingleLocationCorrectly() {
        Location loc = locationService.createLocation(City.SYD, now, now.plusHours(1));

        List<Location> trimmed = locationService.trimLocations(List.of(loc));

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, trimmed.size()),
                () -> Assertions.assertEquals(loc, trimmed.get(0))
        );
    }


}
