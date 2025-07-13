package CommandTests;

import com.company.oop.logistics.commands.FindRoutesServicingStartAndEndCommand;
import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.*;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.modelservices.contracts.RouteService;
import com.company.oop.logistics.modelservices.contracts.VehicleService;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.services.AssignmentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.time.LocalDateTime;
import java.util.List;

public class FindRoutesServicingStartAndEndCommandTest {
    private static final List<String> INVALID_PARAMETERS = List.of("INVALID COMMAND");
    private static final String VALID_CITY = "SYD";
    private static final String VALID_CITY2 = "MEL";
    private static final String INVALID_CITY = "MELS";
    private static final String MESSAGE_NO_ROUTES = "There are no routes servicing %s to %s";

    public static final String TRUCK_NAME = "scania";
    public static final String NO_ASSIGNED_VEHICLE = "No assigned vehicle";
    public static final String FREE_CAPACITY = "Free capacity";

    private RouteService routeService;
    private LocationService locationService;
    private VehicleService vehicleService;
    private AssignmentService assignmentService;
    Command command;

    @BeforeEach
    public void setUp(){
        PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
        locationService = new LocationServiceImpl(persistenceManager);
        routeService = new RouteServiceImpl(persistenceManager, locationService);
        vehicleService = new VehicleServiceImpl(persistenceManager, locationService);
        assignmentService = new AssignmentServiceImpl(
                routeService,
                locationService,
                vehicleService,
                new DeliveryPackageServiceImpl(persistenceManager, locationService));
        command = new FindRoutesServicingStartAndEndCommand(routeService, locationService, assignmentService);
    }

    @Test
    public void execute_Should_Throw_When_InvalidParameterCount(){
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> command.execute(INVALID_PARAMETERS)
        );
    }

    @Test
    public void parseParameters_Should_Throw_When_EitherEnumIsWrong(){
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> command.execute(List.of(VALID_CITY, INVALID_CITY))
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> command.execute(List.of(INVALID_CITY, VALID_CITY))
                )
        );
    }

    @Test
    public void execute_Should_Execute_When_NoRoutes(){
        String result = command.execute(List.of(VALID_CITY, VALID_CITY2));
        Assertions.assertEquals(
                String.format(MESSAGE_NO_ROUTES, City.SYD, City.MEL),
                result
        );
    }

    @Test
    public void execute_Should_Execute_When_ThereAreRoutes(){
        DeliveryRoute route =
                routeService.createDeliveryRoute(LocalDateTime.now().plusMinutes(6), List.of(City.SYD, City.MEL));
        DeliveryRoute route2 =
                routeService.createDeliveryRoute(LocalDateTime.now().plusMinutes(6), List.of(City.SYD, City.MEL));
        Truck vehicle = vehicleService.createVehicle(TRUCK_NAME, City.SYD);
        Truck vehicle2 = vehicleService.createVehicle(TRUCK_NAME, City.SYD);
        assignmentService.assignVehicleToRoute(vehicle2.getId(), route.getId());

        String result = command.execute(List.of(VALID_CITY, VALID_CITY2));

        Assertions.assertAll(
                () -> Assertions.assertTrue(result.contains(NO_ASSIGNED_VEHICLE)),
                () -> Assertions.assertTrue(result.contains(FREE_CAPACITY))
        );
    }

}
