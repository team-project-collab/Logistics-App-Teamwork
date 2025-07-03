package CommandTests;

import com.company.oop.logistics.commands.assign.BulkAssignPackagesCommand;
import com.company.oop.logistics.core.*;
import com.company.oop.logistics.core.contracts.*;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.enums.City;
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
    private LocationService locationService = new LocationServiceImpl();
    private CustomerService customerService;
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

        customerService = new CustomerServiceImpl();
        vehicleService = new VehicleServiceImpl();
        locationService = new LocationServiceImpl();
        routeService = new RouteServiceImpl(vehicleService, locationService);

        customerService.createCustomerContactInfo(
                "Etienne", "+359 8888 8888", "etko8@gmail.com", City.MEL
        );

        vehicleService.createVehicle("scania");

        routeService.createDeliveryRoute(
                LocalDateTime.of(2025, 10, 10, 20, 10),
                new ArrayList<>(List.of(City.SYD, City.MEL, City.ADL))
        );
        routeService.assignVehicleToRoute(vehicleService.getVehicles().get(0).getId(), 1);

        deliveryPackageService = new DeliveryPackageServiceImpl(routeService);

        deliveryPackageService.createDeliveryPackage(
                City.MEL, City.ADL, 40, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(
                City.MEL, City.ADL, 20, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(
                City.MEL, City.ADL, 500, customerService.getCustomerContactById(1));
        deliveryPackageService.createDeliveryPackage(
                City.MEL, City.ADL, 200, customerService.getCustomerContactById(1));

        command = new BulkAssignPackagesCommand(deliveryPackageService);
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
        Assertions.assertEquals(4,routeService.getRouteById(1).assignedPackages().size());
    }

}
