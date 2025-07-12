package com.company.oop.logistics.tests.utils;

import com.company.oop.logistics.core.*;
import com.company.oop.logistics.core.contracts.*;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.Vehicle;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;

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

        PersistenceManager persistenceManager = new PersistenceManager();
        LocationService locationService = new LocationServiceImpl(persistenceManager);
        CustomerService customerService = new CustomerServiceImpl(persistenceManager);
        VehicleService vehicleService = new VehicleServiceImpl(persistenceManager, locationService);
        DeliveryPackageService deliveryPackageService = new DeliveryPackageServiceImpl(persistenceManager, locationService);
        RouteService routeService = new RouteServiceImpl(persistenceManager, vehicleService, locationService, deliveryPackageService);

        customerService.createCustomerContactInfo("Etienne", "+359 8888 8888", "etko8@gmail.com", City.MEL);

        int vehicleId = vehicleService.getVehicles().get(0).getId();
//        for (Truck v:
//             vehicleService.getVehicles()) {
//            System.out.println(v.getLocationIds().stream().toList());
//        }
        LocationServiceImpl locServiceImpl = (LocationServiceImpl) locationService;
        Location sydLocation = locationService.createLocation(City.SYD, LocalDateTime.of(2025, 10, 10, 19, 10), null);
        vehicleService.assignVehicle(vehicleId, List.of(sydLocation.getId()));

        routeService.createDeliveryRoute(
                LocalDateTime.of(2025, 10, 10, 20, 10),
                new ArrayList<>(List.of(City.SYD, City.MEL, City.ADL))
        );
        routeService.assignVehicleToRoute(vehicleService.getVehicles().get(0).getId(), 1);

        deliveryPackageService.createDeliveryPackage(City.MEL, City.ADL, 40, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(City.MEL, City.ADL, 20, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(City.MEL, City.ADL, 500, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(City.MEL, City.ADL, 200, customerService.getCustomerContactById(1));

        return new TestDependencies(deliveryPackageService, routeService, vehicleService, locationService, customerService);
    }

    public static class TestDependencies {
        public final DeliveryPackageService deliveryPackageService;
        public final RouteService routeService;
        public final VehicleService vehicleService;
        public final LocationService locationService;
        public final CustomerService customerService;

        public TestDependencies(DeliveryPackageService deliveryPackageService, RouteService routeService,
                                VehicleService vehicleService, LocationService locationService, CustomerService customerService) {
            this.deliveryPackageService = deliveryPackageService;
            this.routeService = routeService;
            this.vehicleService = vehicleService;
            this.locationService = locationService;
            this.customerService = customerService;
        }
    }
}
