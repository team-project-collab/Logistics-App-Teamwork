package com.company.oop.logistics.tests.modelserivices;

import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.LocationImpl;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.LocationServiceImpl;
import com.company.oop.logistics.modelservices.VehicleServiceImpl;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.modelservices.contracts.VehicleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.time.LocalDateTime;
import java.util.List;

public class VehicleServiceTest {

    private static final City validCity = City.SYD;
    private static final String validName = "scania";
    private VehicleService vehicleService;
    private LocationService locationService;
    private PersistenceManager persistenceManager;


    @BeforeEach
    public void setUp(){
        persistenceManager = new MockPersistenceManagerImpl();
        locationService = new LocationServiceImpl(persistenceManager);
        vehicleService = new VehicleServiceImpl(persistenceManager, locationService);
        TruckImpl.setIds(1001, 1011, 1026);
    }

    @Test
    public void initializeTrucks_Should_PopulateVehiclesList(){
        PersistenceManager persistenceManagerNew = new MockPersistenceManagerImpl();
        LocationService locationServiceNew = new LocationServiceImpl(persistenceManagerNew);
        VehicleService vehicleServiceNew = new VehicleServiceImpl(persistenceManagerNew, locationServiceNew);
        vehicleServiceNew.initializeTrucks();
        Assertions.assertEquals(40, vehicleServiceNew.getAllVehicles().size());
    }


    @Test
    public void createVehicle_Should_AddVehicleToList(){
        vehicleService.createVehicle(validName, validCity);
        Assertions.assertEquals(1, vehicleService.getAllVehicles().size());
    }

    @Test
    public void createVehicle_Should_AssignProperties_When_ValidInputProvided(){
        Truck truck = vehicleService.createVehicle(validName, validCity);
        City truckCity = locationService.getLocationById(truck.getLocationIds().get(0)).getName();
        Assertions.assertAll(
                () -> Assertions.assertTrue(validName.equalsIgnoreCase(truck.getTruckName().toString())),
                () -> Assertions.assertEquals(validCity, truckCity)
        );
    }

    @Test
    public void getVehicleById_Should_ReturnCorrectVehicle_When_IdExists(){
        Truck truck = vehicleService.createVehicle(validName, validCity);
        Assertions.assertEquals(truck, vehicleService.getVehicleById(truck.getId()));
    }

    @Test
    public void getVehicleById_Should_Throw_When_NotExist(){
        Truck truck = vehicleService.createVehicle(validName, validCity);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> vehicleService.getVehicleById(2)
        );
    }

    @Test
    public void getAllVehicles_Should_ReturnVehicles(){
        Truck truck1 = vehicleService.createVehicle(validName, validCity);
        Truck truck2 = vehicleService.createVehicle(validName, validCity);
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, vehicleService.getAllVehicles().size()),
                () -> Assertions.assertEquals(truck1, vehicleService.getAllVehicles().get(0)),
                () -> Assertions.assertEquals(truck2, vehicleService.getAllVehicles().get(1))
        );
    }

    @Test
    public void getAllVehicles_Should_ReturnCopyOfVehicleList(){
        Truck truck1 = vehicleService.createVehicle(validName, validCity);
        vehicleService.getAllVehicles().clear();
        Assertions.assertEquals(1, vehicleService.getAllVehicles().size());
    }

    @Test
    public void isVehicleFree_Should_ReturnTrue_When_TimeAfterLastArrival(){
        LocalDateTime now = LocalDateTime.now();
        Truck truck = vehicleService.createVehicle(validName, validCity);
        Location mockLocation1 = locationService.createLocation(
                City.SYD,
                null,
                now.plusHours(2));
        Location mockLocation2 = locationService.createLocation(
                City.ADL,
                now.plusHours(2),
                now.plusHours(3));
        Location mockLocation3 = locationService.createLocation(
                City.MEL,
                now.plusHours(4),
                now.plusHours(5));

        vehicleService.assignVehicle(truck.getId(), List.of(2, 3, 4));

        Assertions.assertTrue(vehicleService.isVehicleFree(truck.getId(), now.plusHours(6)));

    }

    @Test
    public void isVehicleFree_Should_ReturnFalse_When_TimeBeforeOrEqualToLastArrival(){
        LocalDateTime now = LocalDateTime.now();
        Truck truck = vehicleService.createVehicle(validName, City.SYD);
        Location mockLocation1 = locationService.createLocation(
                City.SYD,
                null,
                now.plusHours(2));
        Location mockLocation2 = locationService.createLocation(
                City.ADL,
                now.plusHours(2),
                now.plusHours(3));
        Location mockLocation3 = locationService.createLocation(
                City.MEL,
                now.plusHours(4),
                now.plusHours(5));
        vehicleService.assignVehicle(truck.getId(), List.of(2, 3, 4));
        Assertions.assertFalse(vehicleService.isVehicleFree(truck.getId(), now.plusHours(2)));
    }

    @Test
    public void isVehicleFree_Should_Throw_When_VehicleIdDoesNotExist(){
        Truck truck = vehicleService.createVehicle(validName, validCity);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> vehicleService.isVehicleFree(2, LocalDateTime.now()));

    }

    @Test
    public void getCurrentLocation_Should_ReturnCurrentLocation(){
        Truck truck = vehicleService.createVehicle(validName, validCity);
        Assertions.assertEquals(
                validCity,
                vehicleService.getCurrentLocation(truck.getId(), LocalDateTime.now()).getName()
        );
    }

    @Test
    public void getCurrentLocation_Should_Throw_When_VehicleIdDoesNotExist(){
        Truck truck = vehicleService.createVehicle(validName, validCity);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> vehicleService.getCurrentLocation(2, LocalDateTime.now()).getName()
        );
    }

}
