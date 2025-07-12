package CommandTests;

import com.company.oop.logistics.commands.assign.BulkAssignPackagesCommand;
import com.company.oop.logistics.core.*;
import com.company.oop.logistics.core.contracts.*;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.contracts.*;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.tests.utils.TestEnvironmentHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BulkAssignTests {
    private BulkAssignPackagesCommand command;
    private DeliveryPackageService deliveryPackageService;
    private RouteService routeService;
    private VehicleService vehicleService;
    private LocationService locationService;
    private CustomerService customerService;
    private AssignmentService assignmentService;
    private static final String DATA_DIR = "data";
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
        command = new BulkAssignPackagesCommand(assignmentService);
    }
    @Test
    public void execute_Should_ThrowError_When_InvalidNumberOfParams(){
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
            command.execute(List.of("111","2"));
                });
    }
    @Test
    public void execute_Should_AssignPackages_When_ValidParamsProvided(){
        command.execute(List.of("1"));
        Assertions.assertEquals(4,routeService.getRouteById(1).getAssignedPackages().size());
    }

    @Test
    public void execute_Should_ReturnNoPackagesToAssignString_When_NoPackages(){
        command.execute(List.of("1"));
        Assertions.assertEquals("No unassigned packages to assign to route 1",command.execute(List.of("1")));
    }

    
    @Test
    public void execute_Should_ThrowError_When_NonNumericRouteId(){
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("abc")));
    }
    
    @Test
    public void execute_Should_HandlePartialAssignment_When_SomePackagesExceedCapacity(){
        deliveryPackageService.createDeliveryPackage(
            City.MEL, City.ADL, 100000, customerService.getCustomerContactById(1)
        );

        String result = command.execute(List.of("1"));

        Assertions.assertEquals(4, routeService.getRouteById(1).getAssignedPackages().size());
        Assertions.assertTrue(result.contains("4 packages added to route 1"));
    }
    
    @Test
    public void execute_Should_HandleEmptyParameters(){
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of()));
    }
    
    @Test
    public void execute_Should_HandleRouteWithoutVehicle(){
        routeService.createDeliveryRoute(
            LocalDateTime.of(2025, 10, 10, 21, 10),
            List.of(City.SYD, City.MEL)
        );

        String result = command.execute(List.of("2"));
        Assertions.assertTrue(result.contains("No unassigned packages to assign to route 2") || 
                            result.contains("0 packages added to route 2"));
    }
    
    @Test
    public void execute_Should_HandleRouteThatDoesNotServicePackageLocations(){
        deliveryPackageService.createDeliveryPackage(
            City.PER, City.BRI, 10, customerService.getCustomerContactById(1)
        );

        String result = command.execute(List.of("1"));
        Assertions.assertEquals(4, routeService.getRouteById(1).getAssignedPackages().size());
    }
    
    @Test
    public void execute_Should_ReturnCorrectMessage_When_AllPackagesAssigned(){
        String result = command.execute(List.of("1"));
        Assertions.assertTrue(result.contains("4 packages added to route 1"));
    }
    
    @Test
    public void execute_Should_HandleMultipleExecutions_WithoutDuplicateAssignments(){
        command.execute(List.of("1"));
        String result = command.execute(List.of("1"));
        Assertions.assertEquals("No unassigned packages to assign to route 1", result);
    }

}
