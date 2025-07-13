package CommandTests;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.commands.creation.CreateRouteCommand;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.modelservices.RouteServiceImpl;
import com.company.oop.logistics.modelservices.LocationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.util.List;

public class CreateRouteCommandTests {
    private static final List<String> INVALID_PARAMETERS = List.of("Param");
    private static final String VALID_START_TIME = "2026-01-01-10:00";
    private static final String VALID_CITY_1 = "SYD";
    private static final String VALID_CITY_2 = "MEL";

    private RouteServiceImpl routeService;
    private Command command;

    @BeforeEach
    public void setUp() {
        PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
        LocationServiceImpl locationService = new LocationServiceImpl(persistenceManager);
        routeService = new RouteServiceImpl(persistenceManager, locationService);
        command = new CreateRouteCommand(routeService);
    }

    @Test
    public void execute_Should_Throw_When_InvalidParameterCount() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> command.execute(INVALID_PARAMETERS)
        );
    }

    @Test
    public void execute_Should_ReturnString_When_RouteCreated() {
        Assertions.assertEquals(
                "Created new route with id: 1",
                command.execute(List.of(VALID_START_TIME, VALID_CITY_1, VALID_CITY_2))
        );
    }
}
