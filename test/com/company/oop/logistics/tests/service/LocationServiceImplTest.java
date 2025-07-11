package com.company.oop.logistics.tests.service;

import com.company.oop.logistics.core.LocationServiceImpl;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.LocationImpl;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationServiceImplTest {

    private PersistenceManager persistenceManager;
    private LocationServiceImpl locationService;


    @BeforeEach
    public void setUp() {

       PersistenceManager persistenceManager = Mockito.mock(PersistenceManager.class);
        Mockito.when(persistenceManager.loadData(Mockito.anyString())).thenReturn(new ArrayList<>());

        locationService = new LocationServiceImpl(persistenceManager);
    }


    @Test
    public void createLocation_Should_CreateAndSaveLocation() {
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);

        Location location = locationService.createLocation(City.ADL, arrivalTime, departureTime);

        Assertions.assertNotNull(location);
        Assertions.assertEquals(City.ADL, location.getName());
        Assertions.assertEquals(arrivalTime, location.getArrivalTime());
        Assertions.assertEquals(departureTime, location.getDepartureTime());

    }

    @Test
    public void getLocations_Should_ReturnAllLocations() {
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);

        locationService.createLocation(City.MEL, arrivalTime, departureTime);

        List<Location> locations = locationService.getLocations();

        Assertions.assertEquals(1, locations.size());
        Assertions.assertEquals(City.MEL, locations.get(0).getName());
    }

    @Test
    public void getLocationById_Should_ReturnCorrectLocation() {
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);

        Location location = locationService.createLocation(City.PER, arrivalTime, departureTime);
        int id = location.getId();

        Location fetchedLocation = locationService.getLocationById(id);

        Assertions.assertEquals(location, fetchedLocation);
    }

    @Test
    public void getLocationById_Should_ThrowException_When_IdNotFound() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> locationService.getLocationById(999));
    }

    @Test
    public void trimLocations_Should_TrimFirstAndLastLocations() {
        LocalDateTime now = LocalDateTime.now();
        Location loc1 = new LocationImpl(1, City.ADL, now, now.plusHours(1));
        Location loc2 = new LocationImpl(2, City.MEL, now.plusHours(2), now.plusHours(3));
        Location loc3 = new LocationImpl(3, City.PER, now.plusHours(4), now.plusHours(5));

        List<Location> inputList = Arrays.asList(loc1, loc2, loc3);

        List<Location> result = locationService.trimLocations(new ArrayList<>(inputList));

        Assertions.assertEquals(3, result.size());
        Assertions.assertNotEquals(loc1, result.get(0));
        Assertions.assertNotEquals(loc3, result.get(2));
        Assertions.assertNull(result.get(0).getArrivalTime());
        Assertions.assertNull(result.get(2).getDepartureTime());
    }

    @Test
    public void trimLocations_Should_DoNothing_When_ListHasOneOrNoElement() {
        LocalDateTime now = LocalDateTime.now();
        Location loc1 = new LocationImpl(1, City.SYD, now, now.plusHours(1));
        List<Location> inputList = new ArrayList<>();
        inputList.add(loc1);

        List<Location> result = locationService.trimLocations(new ArrayList<>(inputList));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(loc1, result.get(0));
    }

}
