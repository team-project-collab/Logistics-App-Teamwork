package CommandTests;

import com.company.oop.logistics.commands.GetUnassignedPackagesCommand;
import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.CustomerServiceImpl;
import com.company.oop.logistics.modelservices.DeliveryPackageServiceImpl;
import com.company.oop.logistics.modelservices.LocationServiceImpl;
import com.company.oop.logistics.modelservices.contracts.DeliveryPackageService;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.util.List;

public class GetUnassignedPackagesCommandTests {
    private static final List<String> INVALID_PARAMETERS = List.of("Param");
    private static final String VALID_NAME = "Gosho";
    private static final String VALID_PHONE = "0888123456";
    private static final String VALID_EMAIL = "asd@abv.bg";

    public static final String MESSAGE_NO_PACKAGES = "There are no unassigned packages.";
    public static final String MESSAGE_LIST_PACKAGES = "Here is the list of unassigned packages:";

    private Command command;
    private CustomerServiceImpl customerService;
    private DeliveryPackageService deliveryPackageService;
    private LocationService locationService;

    @BeforeEach
    public void setUp(){
        PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
        customerService = new CustomerServiceImpl(persistenceManager);
        locationService = new LocationServiceImpl(persistenceManager);
        deliveryPackageService = new DeliveryPackageServiceImpl(persistenceManager, locationService);
        command = new GetUnassignedPackagesCommand(deliveryPackageService);
    }

    @Test
    public void execute_Should_Throw_When_InvalidParameterCount(){
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> command.execute(INVALID_PARAMETERS)
        );
    }

    @Test
    public void execute_Should_ReturnString_When_NoUnassignedPackages(){
        Assertions.assertEquals(
                MESSAGE_NO_PACKAGES,
                command.execute(List.of())
        );
    }

    @Test
    public void execute_Should_ReturnString_When_SomeUnassignedPackages(){
        CustomerContactInfo customer =
                customerService.createCustomerContactInfo(VALID_NAME, VALID_PHONE, VALID_EMAIL, City.MEL);
        deliveryPackageService.createDeliveryPackage(City.SYD, City.MEL, 20, customer);
        Assertions.assertTrue(
                command.execute(List.of()).contains(MESSAGE_LIST_PACKAGES)
        );
        ;
    }
}
