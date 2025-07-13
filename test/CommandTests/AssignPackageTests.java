package CommandTests;

import com.company.oop.logistics.commands.assign.AssignPackageCommand;
import com.company.oop.logistics.commands.assign.BulkAssignPackagesCommand;
import com.company.oop.logistics.core.contracts.*;
import com.company.oop.logistics.exceptions.custom.LimitBreak;
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

public class AssignPackageTests {
    private AssignPackageCommand command;
    private DeliveryPackageService deliveryPackageService;
    private RouteService routeService;
    private VehicleService vehicleService;
    private LocationService locationService;
    private CustomerService customerService;
    private AssignmentService assignmentService;
    private static final String DATA_DIR = "data";
    @BeforeEach
    public void setUp() {
        com.company.oop.logistics.tests.utils.TestEnvironmentHelper.cleanDataDirectory("data");

        TruckImpl.resetTruckLimit();
        TestEnvironmentHelper.TestDependencies deps = TestEnvironmentHelper.initializeServices("data");
        deliveryPackageService = deps.deliveryPackageService;
        routeService = deps.routeService;
        vehicleService = deps.vehicleService;
        locationService = deps.locationService;
        customerService = deps.customerService;
        assignmentService = deps.assignmentService;
        command = new AssignPackageCommand(assignmentService,routeService);
    }
    @Test
    public void execute_Should_ThrowError_When_InvalidNumberOfParams(){
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    command.execute(List.of("111","2","111"));
                });
    }

    @Test
    public void execute_Should_ThrowError_When_RouteDoesNotServicePackage() {
        int packageId = deliveryPackageService.createDeliveryPackage(
            City.PER,
            City.BRI,
            10,
            customerService.getCustomerContactById(1)
        ).getId();
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> command.execute(List.of(String.valueOf(packageId), "1")));
    }

    @Test
    public void execute_Should_ThrowError_When_PackageAlreadyAssigned() {
        command.execute(List.of("1", "1"));
        Assertions.assertThrows(RuntimeException.class,
            () -> command.execute(List.of("1", "1")));
    }

    @Test
    public void execute_Should_ThrowError_When_NonexistentPackage() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> command.execute(List.of("9999", "1")));
    }
    @Test
    public void execute_Should_ThrowError_When_NonexistentRoute() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> command.execute(List.of("1", "9999")));
    }
}
