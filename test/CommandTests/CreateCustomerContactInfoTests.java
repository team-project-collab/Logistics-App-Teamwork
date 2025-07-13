package CommandTests;

import com.company.oop.logistics.commands.GetUnassignedPackagesCommand;
import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.commands.creation.CreateCustomerContactInfo;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.CustomerServiceImpl;
import com.company.oop.logistics.modelservices.DeliveryPackageServiceImpl;
import com.company.oop.logistics.modelservices.LocationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.util.List;

public class CreateCustomerContactInfoTests {
    private static final List<String> INVALID_PARAMETERS = List.of("Param");
    private static final String VALID_NAME = "Etko";
    private static final String VALID_PHONE = "0888888888";
    private static final String VALID_EMAIL = "etko7777@gmail.com";


    private CustomerServiceImpl customerService;
    private Command command;

    @BeforeEach
    public void setUp(){
        PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
        customerService = new CustomerServiceImpl(persistenceManager);
        command = new CreateCustomerContactInfo(customerService);
    }

    @Test
    public void execute_Should_Throw_When_InvalidParameterCount(){
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> command.execute(INVALID_PARAMETERS)
        );
    }

    @Test
    public void execute_Should_ReturnString_When_CustomerInfoCreated(){
        Assertions.assertEquals(
                "Created new customer with id: 1",
                command.execute(List.of(VALID_NAME,VALID_PHONE,VALID_EMAIL,"SYD"))
        );
    }
}
