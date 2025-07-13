package com.company.oop.logistics.tests.services;

import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.db.PersistenceManagerImpl;
import com.company.oop.logistics.exceptions.custom.LimitBreak;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.Vehicle;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.*;
import com.company.oop.logistics.modelservices.contracts.*;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.services.AssignmentServiceImpl;
import net.sf.cglib.core.Local;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockLocationServiceImpl;
import testingUtils.MockPersistenceManagerImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AssignmentServiceTest {
    private static final List<City> validCities = List.of(City.SYD, City.ADL, City.MEL);
    private static final LocalDateTime now = LocalDateTime.now();
    private static final String validTruck = "scania";
    private final PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
    private VehicleService vehicleService;
    private LocationService locationService;
    private DeliveryPackageService deliveryPackageService;
    private RouteService routeService;
    private AssignmentService assignmentService;
    private CustomerContactInfo customerContactInfo;
    private DeliveryPackage deliveryPackage;

    public void setUpMockLocation(){
        locationService = new MockLocationServiceImpl(persistenceManager);
        deliveryPackageService = new DeliveryPackageServiceImpl(persistenceManager, locationService);
        routeService = new RouteServiceImpl(persistenceManager, locationService);
        vehicleService = new VehicleServiceImpl(persistenceManager, locationService);
        TruckImpl.setIds(1001, 1011, 1026);
        assignmentService = new AssignmentServiceImpl(
                routeService,
                locationService,
                vehicleService,
                deliveryPackageService
        );

        customerContactInfo = new CustomerContactInfo(
                1,
                "Test name",
                "0888123456",
                "testmail@abv.bg",
                City.SYD);

        deliveryPackage = deliveryPackageService.createDeliveryPackage(
                City.SYD,
                City.MEL,
                40,
                customerContactInfo);
    }

    @BeforeEach
    public void setUp(){
        locationService = new LocationServiceImpl(persistenceManager);
        deliveryPackageService = new DeliveryPackageServiceImpl(persistenceManager, locationService);
        routeService = new RouteServiceImpl(persistenceManager, locationService);
        vehicleService = new VehicleServiceImpl(persistenceManager, locationService);
        TruckImpl.setIds(1001, 1011, 1026);
        assignmentService = new AssignmentServiceImpl(
                routeService,
                locationService,
                vehicleService,
                deliveryPackageService
        );

        customerContactInfo = new CustomerContactInfo(
                1,
                "Test name",
                "0888123456",
                "testmail@abv.bg",
                City.SYD);

        deliveryPackage = deliveryPackageService.createDeliveryPackage(
                City.SYD,
                City.MEL,
                40,
                customerContactInfo);
    }

    //assignVehicleToRoute tests
    @Test
    public void assignVehicleToRoute_Should_AssignVehicleSuccessfully() {
        Truck vehicle = vehicleService.createVehicle(validTruck, City.SYD);
        DeliveryRoute route = routeService.createDeliveryRoute(now.plusHours(1), List.of(City.SYD, City.ADL, City.MEL));
        assignmentService.assignVehicleToRoute(vehicle.getId(), route.getId());
        Assertions.assertAll(
                () -> Assertions.assertEquals(4, vehicle.getLocationIds().size()),
                () -> Assertions.assertEquals(vehicle.getId(), route.getAssignedVehicleId())
        );
    }

    @Test
    public void assignVehicleToRoute_Should_ThrowException_When_VehicleRangeIsTooLow() {
        List<City> longRoute = List.of(City.BRI, City.PER, City.DAR, City.MEL, City.ASP, City.SYD, City.ADL);
        Truck vehicle = vehicleService.createVehicle(validTruck, City.SYD);
        DeliveryRoute route = routeService.createDeliveryRoute(now.plusHours(1), longRoute);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignVehicleToRoute(vehicle.getId(), route.getId())
        );
    }

    @Test
    public void assignVehicleToRoute_Should_ThrowException_When_VehicleIsAlreadyAssigned() {
        Truck vehicle = vehicleService.createVehicle(validTruck, City.SYD);
        DeliveryRoute route1 = routeService.createDeliveryRoute(now.plusHours(1), validCities);
        DeliveryRoute route2 = routeService.createDeliveryRoute(now.plusHours(1), validCities);
        assignmentService.assignVehicleToRoute(vehicle.getId(), route1.getId());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignVehicleToRoute(vehicle.getId(), route2.getId())
        );
    }

    @Test
    public void assignVehicleToRoute_Should_ThrowException_When_VehicleIsInWrongCity() {
        Truck vehicle = vehicleService.createVehicle(validTruck, City.MEL);
        DeliveryRoute route = routeService.createDeliveryRoute(now.plusHours(1), validCities);
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> assignmentService.assignVehicleToRoute(vehicle.getId(), route.getId())
        );
    }

    // assignPackage tests
    @Test
    public void assignPackage_Should_AssignPackageSuccessfully() {
        DeliveryRoute route = routeService.createDeliveryRoute(now.plusHours(1), validCities);
        Truck truck = vehicleService.createVehicle(validTruck, City.SYD);
        assignmentService.assignVehicleToRoute(truck.getId(), route.getId());
        assignmentService.assignPackage(route.getId(), deliveryPackage.getId());
        Assertions.assertEquals(route.getId(), deliveryPackage.getAssignedRoute());
    }

    @Test
    public void assignPackage_Should_ThrowException_When_NoVehicleAssignedToRoute() {
        DeliveryRoute route = routeService.createDeliveryRoute(now.plusHours(1), validCities);
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> assignmentService.assignPackage(route.getId(), deliveryPackage.getId())
        );
    }

    @Test
    public void assignPackage_Should_ThrowException_When_TruckCapacityExceeded() {
        DeliveryRoute route = routeService.createDeliveryRoute(now.plusHours(1), validCities);
        Truck truck = vehicleService.createVehicle(validTruck, City.SYD);
        DeliveryPackage bigPackage = deliveryPackageService.createDeliveryPackage(
                City.SYD,
                City.ADL,
                truck.getCapacity() - 1,
                customerContactInfo);
        DeliveryPackage bigPackageAfterIt = deliveryPackageService.createDeliveryPackage(
                City.ADL,
                City.MEL,
                truck.getCapacity() - 40,
                customerContactInfo);

        assignmentService.assignVehicleToRoute(truck.getId(), route.getId());
        assignmentService.assignPackage(route.getId(), bigPackage.getId());
        assignmentService.assignPackage(route.getId(), bigPackageAfterIt.getId());

        Assertions.assertThrows(
                LimitBreak.class,
                () -> assignmentService.assignPackage(route.getId(), deliveryPackage.getId())
        );
    }

    @Test
    public void assignPackage_Should_ThrowException_When_RouteAlreadyStarted() {
        setUpMockLocation();
        DeliveryRoute route = routeService.createDeliveryRoute(now.plusHours(1), validCities);
        Truck truck = vehicleService.createVehicle(validTruck, City.SYD);
        assignmentService.assignVehicleToRoute(truck.getId(), route.getId());
        locationService.getLocationById(route.getLocations().get(0)).setDepartureTime(now.minusHours(1));
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> assignmentService.assignPackage(route.getId(), deliveryPackage.getId())
        );
    }

    @Test
    public void assignPackage_Should_ThrowException_When_PackageIsAlreadyAssigned() {
        DeliveryRoute route = routeService.createDeliveryRoute(now.plusHours(1), validCities);
        Truck truck = vehicleService.createVehicle(validTruck, City.SYD);
        assignmentService.assignVehicleToRoute(truck.getId(), route.getId());
        assignmentService.assignPackage(route.getId(), deliveryPackage.getId());
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> assignmentService.assignPackage(route.getId(), deliveryPackage.getId())
        );

    }

    // bulkAssignPackages tests
    @Test
    public void bulkAssignPackages_Should_AssignAllPackages_When_AllAreValid() {
        DeliveryRoute route = routeService.createDeliveryRoute(now.plusHours(1), validCities);
        Truck truck = vehicleService.createVehicle(validTruck, City.SYD);
        assignmentService.assignVehicleToRoute(truck.getId(), route.getId());
        DeliveryPackage validPackage1 = deliveryPackageService.createDeliveryPackage(
                City.SYD,
                City.ADL,
                20,
                customerContactInfo);
        DeliveryPackage validPackage2 = deliveryPackageService.createDeliveryPackage(
                City.SYD,
                City.MEL,
                20,
                customerContactInfo);
        DeliveryPackage validPackage3 = deliveryPackageService.createDeliveryPackage(
                City.ADL,
                City.MEL,
                20,
                customerContactInfo);
        assignmentService.bulkAssignPackages(route.getId());
        Assertions.assertAll(
                () -> Assertions.assertEquals(route.getId(), validPackage1.getAssignedRoute()),
                () -> Assertions.assertEquals(route.getId(), validPackage2.getAssignedRoute()),
                () -> Assertions.assertEquals(route.getId(), validPackage3.getAssignedRoute())
        );
    }

    @Test
    public void bulkAssignPackages_Should_AssignSomePackages_When_SomeAssignmentsFail() {
        DeliveryRoute route = routeService.createDeliveryRoute(now.plusHours(1), validCities);
        Truck truck = vehicleService.createVehicle(validTruck, City.SYD);
        assignmentService.assignVehicleToRoute(truck.getId(), route.getId());
        DeliveryPackage invalidPackageLocation = deliveryPackageService.createDeliveryPackage(
                City.BRI,
                City.ADL,
                20,
                customerContactInfo);
        DeliveryPackage invalidPackageWeight = deliveryPackageService.createDeliveryPackage(
                City.SYD,
                City.ADL,
                200000,
                customerContactInfo);
        assignmentService.bulkAssignPackages(route.getId());
        Assertions.assertAll(
                () -> Assertions.assertEquals(route.getId(), deliveryPackage.getAssignedRoute()),
                () -> Assertions.assertEquals(0, invalidPackageLocation.getAssignedRoute()),
                () -> Assertions.assertEquals(0, invalidPackageLocation.getAssignedRoute())
        );
    }

    // getMaxLoad tests
    @Test
    public void getMaxLoad_Should_ReturnCorrectMaxLoad_When_ValidSubrouteProvided() {
        DeliveryRoute route = routeService.createDeliveryRoute(now.plusHours(1), validCities);
        Truck truck = vehicleService.createVehicle(validTruck, City.SYD);
        assignmentService.assignVehicleToRoute(truck.getId(), route.getId());
        DeliveryPackage validPackage1 = deliveryPackageService.createDeliveryPackage(
                City.SYD,
                City.ADL,
                20,
                customerContactInfo);
        DeliveryPackage validPackage2 = deliveryPackageService.createDeliveryPackage(
                City.SYD,
                City.MEL,
                30,
                customerContactInfo);
        DeliveryPackage validPackage3 = deliveryPackageService.createDeliveryPackage(
                City.ADL,
                City.MEL,
                70,
                customerContactInfo);
        assignmentService.assignPackage(route.getId(), validPackage1.getId());
        assignmentService.assignPackage(route.getId(), validPackage2.getId());
        assignmentService.assignPackage(route.getId(), validPackage3.getId());

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                    validPackage1.getWeightKg() + validPackage2.getWeightKg(),
                    assignmentService.getMaxLoad(route.getId(), City.SYD, City.ADL)
                ),
                () -> Assertions.assertEquals(
                    validPackage2.getWeightKg() + validPackage3.getWeightKg(),
                    assignmentService.getMaxLoad(route.getId(), City.ADL, City.MEL)
                ),
                () -> Assertions.assertEquals(
                    validPackage2.getWeightKg() + validPackage3.getWeightKg(),
                    assignmentService.getMaxLoad(route.getId(), City.SYD, City.MEL)
                )
        );
    }

    @Test
    public void getMaxLoad_Should_ReturnZero_When_NoPackagesAssignedToSubroute() {
    }

    // getFreeCapacity tests
    @Test
    public void getFreeCapacity_Should_ReturnCorrectFreeCapacity_When_ValidRouteAndSubrouteProvided() {
    }

}
