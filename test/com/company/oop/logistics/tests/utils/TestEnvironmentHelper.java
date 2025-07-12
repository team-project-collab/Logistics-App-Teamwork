package com.company.oop.logistics.tests.utils;

import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.db.PersistenceManagerImpl;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.*;
import com.company.oop.logistics.modelservices.contracts.*;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.services.AssignmentServiceImpl;
import com.company.oop.logistics.utils.misc.InitializeTrucks;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestEnvironmentHelper {

    public static void cleanDataDirectory(String dirPath) {
        File dataDir = new File(dirPath);
        if (dataDir.exists() && dataDir.isDirectory()) {
            for (File file : dataDir.listFiles()) {
                if (file.getName().endsWith(".xml")) {
                    file.delete();
                }
            }
        }
    }

    public static TestDependencies initializeServices(String dataDirPath) {
        cleanDataDirectory(dataDirPath);

        PersistenceManager persistenceManager = new PersistenceManagerImpl();
        LocationService locationService = new LocationServiceImpl(persistenceManager);
        CustomerService customerService = new CustomerServiceImpl(persistenceManager);
        VehicleService vehicleService = new VehicleServiceImpl(persistenceManager, locationService);
        DeliveryPackageService deliveryPackageService = new DeliveryPackageServiceImpl(persistenceManager, locationService);
        RouteService routeService = new RouteServiceImpl(persistenceManager,locationService);
        AssignmentService assignmentService = new AssignmentServiceImpl(routeService,locationService,vehicleService,deliveryPackageService);
        InitializeTrucks.execute(vehicleService);
        customerService.createCustomerContactInfo("Etienne", "+359 8888 8888", "etko8@gmail.com", City.MEL);

        int vehicleId = vehicleService.getAllVehicles().get(0).getId();

        LocationServiceImpl locServiceImpl = (LocationServiceImpl) locationService;
        Location sydLocation = locationService.createLocation(City.SYD, LocalDateTime.of(2025, 10, 10, 19, 10), null);
        vehicleService.assignVehicle(vehicleId, List.of(sydLocation.getId()));

        routeService.createDeliveryRoute(
                LocalDateTime.of(2025, 10, 10, 20, 10),
                new ArrayList<>(List.of(City.SYD, City.MEL, City.ADL))
        );
        routeService.assignVehicle(vehicleService.getAllVehicles().get(0).getId(), 1);

        deliveryPackageService.createDeliveryPackage(City.MEL, City.ADL, 40, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(City.MEL, City.ADL, 20, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(City.MEL, City.ADL, 500, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(City.MEL, City.ADL, 200, customerService.getCustomerContactById(1));

        return new TestDependencies(deliveryPackageService, routeService, vehicleService, locationService, customerService, assignmentService);
    }

    public static class TestDependencies {
        public final DeliveryPackageService deliveryPackageService;
        public final RouteService routeService;
        public final VehicleService vehicleService;
        public final LocationService locationService;
        public final CustomerService customerService;
        public final AssignmentService assignmentService;

        public TestDependencies(DeliveryPackageService deliveryPackageService, RouteService routeService,
                                VehicleService vehicleService, LocationService locationService, CustomerService customerService,
                                AssignmentService assignmentService) {
            this.deliveryPackageService = deliveryPackageService;
            this.routeService = routeService;
            this.vehicleService = vehicleService;
            this.locationService = locationService;
            this.customerService = customerService;
            this.assignmentService = assignmentService;
        }
    }
}
