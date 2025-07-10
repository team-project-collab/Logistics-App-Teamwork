package CommandTests;

import com.company.oop.logistics.commands.assign.BulkAssignPackagesCommand;
import com.company.oop.logistics.core.*;
import com.company.oop.logistics.core.contracts.*;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.tests.utils.TestEnvironmentHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        command = new BulkAssignPackagesCommand(deliveryPackageService, routeService);
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

}
