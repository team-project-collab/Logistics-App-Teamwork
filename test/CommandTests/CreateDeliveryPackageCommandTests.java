package CommandTests;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.commands.creation.CreateCustomerContactInfo;
import com.company.oop.logistics.commands.creation.CreateDeliveryPackageCommand;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.CustomerServiceImpl;
import com.company.oop.logistics.modelservices.DeliveryPackageServiceImpl;
import com.company.oop.logistics.modelservices.LocationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.util.List;

public class CreateDeliveryPackageCommandTests {
    private static final List<String> INVALID_PARAMETERS = List.of("Param");
    private static final String VALID_START_LOCATION = "SYD";
    private static final String VALID_END_LOCATION = "MEL";
    private static final String VALID_WEIGHT = "10.5";
    private static final String VALID_CUSTOMER_ID = "1";

    private DeliveryPackageServiceImpl deliveryPackageService;
    private CustomerServiceImpl customerService;
    private Command command;

    @BeforeEach
    public void setUp() {
        PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
        LocationServiceImpl locationService = new LocationServiceImpl(persistenceManager);
        deliveryPackageService = new DeliveryPackageServiceImpl(persistenceManager, locationService);
        customerService = new CustomerServiceImpl(persistenceManager);
        command = new CreateDeliveryPackageCommand(deliveryPackageService, customerService);
    }

    @Test
    public void execute_Should_Throw_When_InvalidParameterCount() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> command.execute(INVALID_PARAMETERS)
        );
    }

    @Test
    public void execute_Should_ReturnString_When_DeliveryPackageCreated() {
        CreateCustomerContactInfo contact = new CreateCustomerContactInfo(customerService);
        contact.execute(List.of("Etko","0888888888","etko7777@gmail.com","MEL"));
        Assertions.assertEquals(
                "Created new delivery package with id: 1",
                command.execute(List.of(VALID_START_LOCATION, VALID_END_LOCATION, VALID_WEIGHT, VALID_CUSTOMER_ID))
        );
    }
}
