package CommandTests;

import com.company.oop.logistics.commands.assign.BulkAssignPackagesCommand;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.*;
import com.company.oop.logistics.modelservices.contracts.*;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.services.AssignmentServiceImpl;
import org.junit.jupiter.api.Assertions;
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

        File dataDir = new File("data");
        if (dataDir.exists() && dataDir.isDirectory()) {
            for (File file : dataDir.listFiles()) {
                if (file.getName().endsWith(".xml")) {
                    file.delete();
                }
            }
        }
        PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
        customerService = new CustomerServiceImpl(persistenceManager);
        vehicleService = new VehicleServiceImpl(persistenceManager, locationService);
        locationService = new LocationServiceImpl(persistenceManager);
        deliveryPackageService = new DeliveryPackageServiceImpl(persistenceManager, locationService);
        routeService = new RouteServiceImpl(persistenceManager, locationService);
        assignmentService = new AssignmentServiceImpl(routeService, locationService, vehicleService, deliveryPackageService);


        customerService.createCustomerContactInfo(
                "Etienne", "+359 8888 8888", "etko8@gmail.com", City.MEL
        );

        vehicleService.createVehicle("scania", City.SYD);

        routeService.createDeliveryRoute(
                LocalDateTime.of(2025, 10, 10, 20, 10),
                new ArrayList<>(List.of(City.SYD, City.MEL, City.ADL))
        );
        assignmentService.assignVehicleToRoute(vehicleService.getAllVehicles().get(0).getId(), 1);



        deliveryPackageService.createDeliveryPackage(
                City.MEL, City.ADL, 40, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(
                City.MEL, City.ADL, 20, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(
                City.MEL, City.ADL, 500, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(
                City.MEL, City.ADL, 200, customerService.getCustomerContactById(1));

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

}
