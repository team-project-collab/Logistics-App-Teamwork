package com.company.oop.logistics.tests.services;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.*;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.DeliveryPackageServiceImpl;
import com.company.oop.logistics.modelservices.LocationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeliverPackageServiceTest {
    private PersistenceManager persistenceManager;
    private LocationServiceImpl locationService;
    private DeliveryPackageServiceImpl deliveryPackageService;
    private CustomerContactInfo customer;

    @BeforeEach
    public void setUp() {
        persistenceManager = new MockPersistenceManagerImpl();
        locationService = new LocationServiceImpl(persistenceManager);
        deliveryPackageService = new DeliveryPackageServiceImpl(persistenceManager, locationService);


        customer = new CustomerContactInfo(1, "Test Customer", "0888123456", "test@mail.com", City.SYD);
        persistenceManager.saveData(List.of(customer), "data/customers.xml");
    }

    @Test
    public void createDeliveryPackage_Should_CreatePackageSuccessfully() {
        DeliveryPackage dp = deliveryPackageService.createDeliveryPackage(
                City.SYD, City.MEL, 15.5, customer);

        assertNotNull(dp);
        assertEquals(City.SYD, dp.getStartLocation());
        assertEquals(City.MEL, dp.getEndLocation());
        assertEquals(15.5, dp.getWeightKg());
        assertEquals(customer.getId(), dp.getCustomerContactInfoId());
        assertFalse(dp.isAssigned());
        assertNotNull(dp.getLocations());
        assertFalse(dp.getLocations().isEmpty());


        DeliveryPackage fetched = deliveryPackageService.getDeliveryPackageById(dp.getId());
        assertEquals(dp, fetched);
    }

    @Test
    public void getDeliveryPackageById_Should_ReturnCorrectPackage_WhenExists() {
        DeliveryPackage dp = deliveryPackageService.createDeliveryPackage(
                City.ADL, City.MEL, 5, customer);

        DeliveryPackage fetched = deliveryPackageService.getDeliveryPackageById(dp.getId());
        assertEquals(dp, fetched);
    }

    @Test
    public void getDeliveryPackageById_Should_ThrowException_WhenNotFound() {
        assertThrows(IllegalArgumentException.class, () -> deliveryPackageService.getDeliveryPackageById(9999));
    }

    @Test
    public void getAllDeliveryPackages_Should_ReturnAllPackages() {
        deliveryPackageService.createDeliveryPackage(City.SYD, City.MEL, 10, customer);
        deliveryPackageService.createDeliveryPackage(City.ADL, City.SYD, 20, customer);

        List<DeliveryPackage> allPackages = deliveryPackageService.getAllDeliveryPackages();
        assertEquals(2, allPackages.size());
    }

    @Test
    public void getUnassignedPackages_Should_ReturnOnlyUnassignedPackages() {
        DeliveryPackage assignedPackage = deliveryPackageService.createDeliveryPackage(City.SYD, City.MEL, 10, customer);
        DeliveryPackage unassignedPackage = deliveryPackageService.createDeliveryPackage(City.ADL, City.SYD, 20, customer);


        assignedPackage.assign(1);

        List<DeliveryPackage> unassigned = deliveryPackageService.getUnassignedPackages();
        assertTrue(unassigned.contains(unassignedPackage));
        assertFalse(unassigned.contains(assignedPackage));
    }

    @Test
    public void getPackageState_Should_ReturnStatusString() {
        DeliveryPackage dp = deliveryPackageService.createDeliveryPackage(City.SYD, City.MEL, 10, customer);
        String status = deliveryPackageService.getPackageState(dp.getId(), LocalDateTime.now());
        assertNotNull(status);
        assertFalse(status.isBlank());
    }

    @Test
    public void assignPackage_Should_AssignPackageAndSave() {
        DeliveryPackage dp = deliveryPackageService.createDeliveryPackage(City.SYD, City.MEL, 10, customer);

        List<Integer> newLocationIds = List.of(locationService.createLocation(City.SYD, LocalDateTime.now(), null).getId());

        deliveryPackageService.assignPackage(123, dp.getId(), newLocationIds);

        DeliveryPackage updatedPackage = deliveryPackageService.getDeliveryPackageById(dp.getId());
        assertEquals(123, updatedPackage.getAssignedRoute());
        assertEquals(newLocationIds, updatedPackage.getLocations());
        assertTrue(updatedPackage.isAssigned());
    }

}
