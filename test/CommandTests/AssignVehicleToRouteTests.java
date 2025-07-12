package CommandTests;

import com.company.oop.logistics.commands.assign.AssignVehicleToRouteCommand;
import com.company.oop.logistics.core.contracts.*;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.contracts.*;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.tests.utils.TestEnvironmentHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public class AssignVehicleToRouteTests {
    private AssignVehicleToRouteCommand command;
    private DeliveryPackageService deliveryPackageService;
    private RouteService routeService;
    private VehicleService vehicleService;
    private LocationService locationService;
    private CustomerService customerService;
    private AssignmentService assignmentService;

    @BeforeEach
    public void setUp() {
        TestEnvironmentHelper.cleanDataDirectory("data");
        TruckImpl.resetTruckLimit();
        
        TestEnvironmentHelper.TestDependencies deps = TestEnvironmentHelper.initializeServices("data");
        deliveryPackageService = deps.deliveryPackageService;
        routeService = deps.routeService;
        vehicleService = deps.vehicleService;
        locationService = deps.locationService;
        customerService = deps.customerService;
        assignmentService = deps.assignmentService;

        command = new AssignVehicleToRouteCommand(assignmentService);
    }

    @Test
    public void execute_Should_ThrowError_When_InvalidNumberOfParams() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("1")));
    }

    @Test
    public void execute_Should_ThrowError_When_TooManyParams() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("1", "2", "3")));
    }

    @Test
    public void execute_Should_ThrowError_When_NonNumericVehicleId() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("abc", "1")));
    }

    @Test
    public void execute_Should_ThrowError_When_NonNumericRouteId() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("1", "xyz")));
    }

    @Test
    public void execute_Should_ThrowError_When_NonexistentVehicle() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("999", "1")));
    }

    @Test
    public void execute_Should_ThrowError_When_NonexistentRoute() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("1", "999")));
    }

    @Test
    public void execute_Should_ThrowError_When_VehicleInsufficientRange() {
        // Create a route with long distance that exceeds truck range
        routeService.createDeliveryRoute(
            LocalDateTime.of(2025, 10, 10, 22, 10),
            List.of(City.SYD, City.PER, City.ADL, City.BRI, City.MEL)
        );
        
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("1", "2")));
    }

    @Test
    public void execute_Should_ThrowError_When_VehicleAlreadyAssigned() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("1001", "1")));
    }

    @Test
    public void execute_Should_ThrowError_When_VehicleNotInCorrectCity() {
        routeService.createDeliveryRoute(
            LocalDateTime.of(2025, 10, 10, 23, 10),
            List.of(City.PER, City.ADL)
        );
        
        Assertions.assertThrows(IllegalStateException.class,
                () -> command.execute(List.of("1002", "2")));
    }

    @Test
    public void execute_Should_Succeed_When_ValidParameters() {
        String result = command.execute(List.of("1001", "1"));
        Assertions.assertEquals("Vehicle 1 added to route 1", result);

        // Verify vehicle is assigned to route
        Assertions.assertEquals(1, routeService.getRouteById(1).getAssignedVehicleId());
    }

    @Test
    public void execute_Should_Succeed_When_VehicleHasNoPreviousLocations() {
        // Create a new vehicle with no locations
        int newVehicleId = vehicleService.createVehicle("NewTruck", City.SYD).getId();
        
        // Create a route starting from SYD
        routeService.createDeliveryRoute(
            LocalDateTime.of(2025, 10, 10, 24, 10),
            List.of(City.SYD, City.MEL)
        );
        
        String result = command.execute(List.of(String.valueOf(newVehicleId), "2"));
        Assertions.assertEquals("Vehicle " + newVehicleId + " added to route 2", result);
    }

    @Test
    public void execute_Should_HandleEmptyParameters() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of()));
    }


    @Test
    public void execute_Should_HandleNegativeVehicleId() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("-1", "1")));
    }

    @Test
    public void execute_Should_HandleNegativeRouteId() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("1", "-1")));
    }

    @Test
    public void execute_Should_HandleZeroVehicleId() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("0", "1")));
    }

    @Test
    public void execute_Should_HandleZeroRouteId() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("1", "0")));
    }

    @Test
    public void execute_Should_HandleVehicleWithPreviousAssignments() {
        routeService.createDeliveryRoute(
            LocalDateTime.of(2026, 10, 11, 10, 10),
            List.of(City.ADL, City.MEL)
        );

        String result = command.execute(List.of("1001", "2"));
        Assertions.assertEquals("Vehicle 1001 added to route 2", result);
    }

    @Test
    public void execute_Should_VerifyVehicleLocationsAreUpdated() {

        List<Integer> vehicleLocations = vehicleService.getVehicleById(1001).getLocationIds();
        Assertions.assertFalse(vehicleLocations.isEmpty());
        
        // Verify route locations match vehicle locations
        List<Integer> routeLocations = routeService.getRouteById(1).getLocations();
        Assertions.assertEquals(routeLocations, vehicleLocations);
    }
} 