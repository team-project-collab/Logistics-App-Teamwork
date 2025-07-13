package CommandTests;

import com.company.oop.logistics.commands.listing.ListVehiclesCommand;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListVehiclesCommandTests {
    private ListVehiclesCommand command;
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
        command = new ListVehiclesCommand(vehicleService, locationService);
    }

    @Test
    public void execute_Should_ReturnAllVehicles_When_NoParameters() {
        String result = command.execute(List.of());
        
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(result.contains("Truck id:"));
        Assertions.assertTrue(result.contains("==="));
    }

    @Test
    public void execute_Should_ReturnAllVehicles_When_EmptyParameters() {
        String result = command.execute(List.of());
        
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(result.contains("Truck id:"));
    }

    @Test
    public void execute_Should_ReturnAllVehicles_When_NullParameters() {
        String result = command.execute(null);
        
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(result.contains("Truck id:"));
    }

    @Test
    public void execute_Should_FilterByCity_When_ValidCityParameter() {
        String result = command.execute(List.of("SYD"));
        
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("Truck id:"));
        // Should only show vehicles in SYD
        Assertions.assertTrue(result.contains("Sydney"));
    }

    @Test
    public void execute_Should_FilterByCity_When_CityParameterInLowerCase() {
        String result = command.execute(List.of("syd"));
        
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("Truck id:"));
    }

    @Test
    public void execute_Should_FilterByCity_When_CityParameterInUpperCase() {
        int vehicleId = vehicleService.createVehicle("scania", City.ADL).getId();
        String result = command.execute(List.of("ADL"));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains(String.format("Truck id: %d", vehicleId)));
    }

    @Test
    public void execute_Should_ShowFreeVehicles_When_FreeModifier() {
        String result = command.execute(List.of("free"));
        
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("Truck id:") || result.contains("No vehicles found"));
    }

    @Test
    public void execute_Should_ShowFreeVehicles_When_FreeModifierInUpperCase() {
        String result = command.execute(List.of("FREE"));
        
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("Truck id:") || result.contains("No vehicles found"));
    }

    @Test
    public void execute_Should_ShowFreeVehicles_When_FreeModifierInMixedCase() {
        String result = command.execute(List.of("Free"));
        
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("Truck id:") || result.contains("No vehicles found"));
    }

    @Test
    public void execute_Should_ThrowError_When_InvalidModifier() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("invalid")));
    }

    @Test
    public void execute_Should_ThrowError_When_TooManyParameters() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("SYD", "free")));
    }

    @Test
    public void execute_Should_ReturnNoVehiclesMessage_When_NoVehiclesInCity() {
        String result = command.execute(List.of("PER"));
        
        Assertions.assertTrue(result.contains("No vehicles found with this search"));
    }

    @Test
    public void execute_Should_ReturnNoVehiclesMessage_When_NoFreeVehicles() {
        // All vehicles are assigned in the test setup, so none should be free
        String result = command.execute(List.of("free"));
        
        Assertions.assertTrue(result.contains("No vehicles found with this search"));
    }

    @Test
    public void execute_Should_IncludeVehicleDetails_When_VehiclesExist() {
        String result = command.execute(List.of());
        
        Assertions.assertTrue(result.contains("Brand:"));
        Assertions.assertTrue(result.contains("Max load:"));
        Assertions.assertTrue(result.contains("==="));
    }

    @Test
    public void execute_Should_HandleInvalidCityCode() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("INVALID")));
    }

    @Test
    public void execute_Should_HandleSpecialCharacters() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("!@#$%")));
    }

    @Test
    public void execute_Should_HandleNumbersAsCityCode() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("123")));
    }

    @Test
    public void execute_Should_HandleVeryLongCityCode() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("A".repeat(100))));
    }

    @Test
    public void execute_Should_HandleUnicodeCityCode() {
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
    public void execute_Should_HandleMultipleValidCities() {
        // Test different cities that might have vehicles
        String result1 = command.execute(List.of("SYD"));
        String result2 = command.execute(List.of("MEL"));
        
        Assertions.assertNotNull(result1);
        Assertions.assertNotNull(result2);
    }

    @Test
    public void execute_Should_IncludeLocationInfo_When_VehiclesHaveLocations() {
        String result = command.execute(List.of());
        String regex = "===\\s*"
                + "Truck id: \\d+, Brand: .+?, Max load: \\d+\\s*"
                + "Assigned at .+? and ready to depart to .+? at \\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}\\s*"
                + "===";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(result);

        // Should include location information in the output
        Assertions.assertTrue(matcher.find());
    }
}