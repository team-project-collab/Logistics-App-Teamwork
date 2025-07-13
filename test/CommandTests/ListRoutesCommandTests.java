package CommandTests;

import com.company.oop.logistics.commands.listing.ListRoutesCommand;
import com.company.oop.logistics.core.contracts.*;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.DeliveryPackageServiceImpl;
import com.company.oop.logistics.modelservices.LocationServiceImpl;
import com.company.oop.logistics.modelservices.RouteServiceImpl;
import com.company.oop.logistics.modelservices.VehicleServiceImpl;
import com.company.oop.logistics.modelservices.contracts.*;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.services.AssignmentServiceImpl;
import com.company.oop.logistics.tests.utils.TestEnvironmentHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.time.LocalDateTime;
import java.util.List;

public class ListRoutesCommandTests {
    private ListRoutesCommand command;
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
        command = new ListRoutesCommand(routeService,locationService,assignmentService);
    }

    @Test
    public void execute_Should_ReturnAllRoutes_When_NoParameters() {
        String result = command.execute(List.of());
        
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(result.contains("Route id:"));
        Assertions.assertTrue(result.contains("==="));
    }

    @Test
    public void execute_Should_ReturnAllRoutes_When_EmptyParameters() {
        String result = command.execute(List.of());
        
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(result.contains("Route id:"));
    }

    @Test
    public void execute_Should_ShowActiveRoutes_When_ActiveModifier() {
        String result = command.execute(List.of("active"));
        
        Assertions.assertNotNull(result);
        // Should either show active routes or "no routes" message
        Assertions.assertTrue(result.contains("Route id:") || result.contains("There are no routes matching the criteria"));
    }

    @Test
    public void execute_Should_ShowActiveRoutes_When_ActiveModifierInUpperCase() {
        String result = command.execute(List.of("ACTIVE"));
        
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("Route id:") || result.contains("There are no routes matching the criteria"));
    }

    @Test
    public void execute_Should_ShowActiveRoutes_When_ActiveModifierInMixedCase() {
        String result = command.execute(List.of("Active"));
        
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("Route id:") || result.contains("There are no routes matching the criteria"));
    }

    @Test
    public void execute_Should_ThrowError_When_InvalidModifier() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("invalid")));
    }

    @Test
    public void execute_Should_ThrowError_When_TooManyParameters() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("active", "extra")));
    }

    @Test
    public void execute_Should_ReturnNoRoutesMessage_When_NoRoutesExist() {
        // Clean up and create fresh environment without routes

        PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
        LocationService emptyLocationService = new LocationServiceImpl(persistenceManager);
        VehicleService emptyVehicleService = new VehicleServiceImpl(persistenceManager, emptyLocationService);
        DeliveryPackageService emptyDeliveryPackageService = new DeliveryPackageServiceImpl(persistenceManager,
                emptyLocationService);
        RouteService emptyRouteService = new RouteServiceImpl(persistenceManager,locationService);
        AssignmentService emptyAssignmentService = new AssignmentServiceImpl(
                emptyRouteService,
                emptyLocationService,
                emptyVehicleService,
                emptyDeliveryPackageService
        );
        
        ListRoutesCommand emptyCommand = new ListRoutesCommand(
                emptyRouteService,
                emptyLocationService,
                emptyAssignmentService
        );
        String result = emptyCommand.execute(List.of());
        
        Assertions.assertEquals("There are no routes matching the criteria", result);
    }

    @Test
    public void execute_Should_IncludeRouteDetails_When_RoutesExist() {
        String result = command.execute(List.of());
        
        Assertions.assertTrue(result.contains("from"));
        Assertions.assertTrue(result.contains("to"));
        Assertions.assertTrue(result.contains("Total distance:"));
    }

    @Test
    public void execute_Should_HandleRoutesWithAssignedVehicles() {
        String result = command.execute(List.of());
        
        // Should show vehicle assignment info
        Assertions.assertTrue(result.contains("Assigned truck id:") || result.contains("No assigned truck yet"));
    }

    @Test
    public void execute_Should_HandleRoutesWithoutAssignedVehicles() {
        // Create a new route without assigning a vehicle
        routeService.createDeliveryRoute(
            LocalDateTime.of(2025, 10, 10, 21, 10),
            List.of(City.SYD, City.MEL)
        );
        
        String result = command.execute(List.of());
        
        Assertions.assertTrue(result.contains("No assigned truck yet"));
    }

    @Test
    public void execute_Should_HandleSpecialCharacters() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("!@#$%")));
    }

    @Test
    public void execute_Should_HandleNumbersAsModifier() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("123")));
    }

    @Test
    public void execute_Should_HandleEmptyModifier() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("")));
    }

    @Test
    public void execute_Should_HandleVeryLongModifier() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("A".repeat(100))));
    }

    @Test
    public void execute_Should_HandleUnicodeModifier() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("测试")));
    }

    @Test
    public void execute_Should_ReturnConsistentOutput_When_CalledMultipleTimes() {
        String result1 = command.execute(List.of());
        String result2 = command.execute(List.of());
        
        Assertions.assertEquals(result1, result2);
    }

    @Test
    public void execute_Should_HandleActiveRoutes_When_SomeRoutesAreActive() {
        routeService.createDeliveryRoute(
            LocalDateTime.now().plusDays(2),
            List.of(City.SYD, City.MEL, City.ADL)
        );
        
        String result = command.execute(List.of("active"));
        
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("Route id:") || result.contains("There are no routes matching the criteria"));
    }

    @Test
    public void execute_Should_HandleInactiveRoutes_When_AllRoutesAreInactive() {
        routeService.createDeliveryRoute(
            LocalDateTime.now().plusDays(1),
            List.of(City.SYD, City.MEL)
        );
        
        String result = command.execute(List.of("active"));
        
        Assertions.assertTrue(result.contains("There are no routes matching the criteria"));
    }

    @Test
    public void execute_Should_IncludeLocationDetails_When_RoutesHaveLocations() {
        String result = command.execute(List.of());

        Assertions.assertTrue(result.contains("Sydney") || result.contains("Melbourne") || result.contains("Adelaide")
                || result.contains("Perth")|| result.contains("Alice Springs")|| result.contains("Brisbane")|| result.contains("Darwin"));
    }

    @Test
    public void execute_Should_HandleRoutesWithLoadInformation() {
        String result = command.execute(List.of());
        
        // Should include load and capacity information
        Assertions.assertTrue(result.contains("Current load:") || result.contains("Free capacity:"));
    }

    @Test
    public void execute_Should_HandleRoutesWithDistanceInformation() {
        String result = command.execute(List.of());
        
        // Should include distance information
        Assertions.assertTrue(result.contains("Total distance:"));
    }

    @Test
    public void execute_Should_HandleMultipleRoutes() {
        // Create additional routes
        routeService.createDeliveryRoute(
            LocalDateTime.of(2025, 10, 10, 22, 10),
            List.of(City.MEL, City.ADL)
        );
        
        String result = command.execute(List.of());
        
        // Should show multiple routes
        Assertions.assertTrue(result.contains("Route id:"));
        // Count the number of route entries
        long routeCount = result.chars().filter(ch -> ch == '=').count() / 2; // Each route has ===
        Assertions.assertTrue(routeCount >= 2);
    }

    @Test
    public void execute_Should_HandleRoutesWithPackages() {
        String result = command.execute(List.of());
        
        // Should handle routes that have assigned packages
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("Route id:"));
    }
} 