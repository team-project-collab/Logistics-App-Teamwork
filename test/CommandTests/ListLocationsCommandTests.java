package CommandTests;

import com.company.oop.logistics.commands.listing.ListLocationsCommand;
import com.company.oop.logistics.core.contracts.*;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.tests.utils.TestEnvironmentHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public class ListLocationsCommandTests {
    private ListLocationsCommand command;
    private DeliveryPackageService deliveryPackageService;
    private RouteService routeService;
    private VehicleService vehicleService;
    private LocationService locationService;
    private CustomerService customerService;

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
        command = new ListLocationsCommand(locationService);
    }

    @Test
    public void execute_Should_ReturnAllLocations_When_NoParameters() {
        String result = command.execute(List.of());

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());

    }

    @Test
    public void execute_Should_ReturnAllLocations_When_EmptyParameters() {
        String result = command.execute(List.of(""));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void execute_Should_ReturnAllLocations_When_NullParameters() {
        String result = command.execute(null);
        
        // Should contain location information
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void execute_Should_ReturnAllLocations_When_MultipleParameters() {
        String result = command.execute(List.of("param1", "param2"));
        
        // Should contain location information regardless of parameters
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void execute_Should_ReturnEmptyString_When_NoLocationsExist() {
        // Clean up and create fresh environment without locations
        TestEnvironmentHelper.cleanDataDirectory("data");
        
        // Create services without initializing test data
        com.company.oop.logistics.db.PersistenceManager persistenceManager = new com.company.oop.logistics.db.PersistenceManager();
        LocationService emptyLocationService = new com.company.oop.logistics.core.LocationServiceImpl(persistenceManager);
        
        ListLocationsCommand emptyCommand = new ListLocationsCommand(emptyLocationService);
        String result = emptyCommand.execute(List.of());
        
        Assertions.assertEquals("", result);
    }

    @Test
    public void execute_Should_IncludeLocationDetails_When_LocationsExist() {
        String result = command.execute(List.of());

        Assertions.assertTrue(result.contains("Sydney") || result.contains("Melbourne") || result.contains("Adelaide")
        || result.contains("Perth")|| result.contains("Alice Springs")|| result.contains("Brisbane")|| result.contains("Darwin"));
    }

    @Test
    public void execute_Should_HandleSpecialCharacters_InParameters() {
        String result = command.execute(List.of("!@#$%^&*()"));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void execute_Should_ReturnConsistentOutput_When_CalledMultipleTimes() {
        String result1 = command.execute(List.of());
        String result2 = command.execute(List.of());
        
        Assertions.assertEquals(result1, result2);
    }

    @Test
    public void execute_Should_HandleVeryLongParameters() {
        String longParam = "a".repeat(1000);
        String result = command.execute(List.of(longParam));
        
        // Should still return locations
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }
} 