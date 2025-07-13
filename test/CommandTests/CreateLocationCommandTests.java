package CommandTests;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.commands.creation.CreateLocationCommand;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.modelservices.LocationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.util.List;

public class CreateLocationCommandTests {
    private static final List<String> INVALID_PARAMETERS = List.of("Param");
    private static final String VALID_LOCATION = "SYD";
    private static final String VALID_ARRIVAL_TIME = "2026-01-01-10:00";
    private static final String VALID_DEPARTURE_TIME = "2026-01-01-12:00";

    private LocationServiceImpl locationService;
    private Command command;

    @BeforeEach
    public void setUp() {
        PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
        locationService = new LocationServiceImpl(persistenceManager);
        command = new CreateLocationCommand(locationService);
    }

    @Test
    public void execute_Should_Throw_When_InvalidParameterCount() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> command.execute(INVALID_PARAMETERS)
        );
    }

    @Test
    public void execute_Should_ReturnString_When_LocationCreated() {
        Assertions.assertEquals(
                "Created new location at Sydney from 2026-01-01T10:00 to 2026-01-01T12:00",
                command.execute(List.of(VALID_LOCATION, VALID_ARRIVAL_TIME, VALID_DEPARTURE_TIME))
        );
    }
}
