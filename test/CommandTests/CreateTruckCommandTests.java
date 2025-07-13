package CommandTests;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.commands.creation.CreateTruckCommand;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.modelservices.VehicleServiceImpl;
import com.company.oop.logistics.modelservices.LocationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.util.List;

public class CreateTruckCommandTests {
    private static final List<String> INVALID_PARAMETERS = List.of("Param");
    private static final String VALID_TRUCK_NAME = "Scania";
    private static final String VALID_CITY = "SYD";

    private VehicleServiceImpl vehicleService;
    private Command command;

    @BeforeEach
    public void setUp() {
        PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
        LocationServiceImpl locationService = new LocationServiceImpl(persistenceManager);
        vehicleService = new VehicleServiceImpl(persistenceManager, locationService);
        command = new CreateTruckCommand(vehicleService);
    }

    @Test
    public void execute_Should_Throw_When_InvalidParameterCount() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> command.execute(INVALID_PARAMETERS)
        );
    }

    @Test
    public void execute_Should_ReturnString_When_TruckCreated() {
        TruckImpl.resetTruckLimit();
        Assertions.assertEquals(
                "Created truck Scania with id 1001\n",
                command.execute(List.of(VALID_TRUCK_NAME, VALID_CITY))
        );
    }
}
