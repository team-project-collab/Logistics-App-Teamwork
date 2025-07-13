package CommandTests;

import com.company.oop.logistics.commands.GetPackageStateCommand;
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

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;

public class GetPackageStateCommandTest {
    private static final List<String> INVALID_PARAMETERS = List.of("Param");
    private static final String VALID_NAME = "Gosho";
    private static final String VALID_PHONE = "0888123456";
    private static final String VALID_EMAIL = "asd@abv.bg";
    public static final String NOW_KEYWORD = "now";
    public static final String INVALID_NUMBER = "asd";
    public static final String INVALID_DATE = "invalid date";

    private CustomerContactInfo customerContactInfo;
    private CustomerServiceImpl customerService;
    private LocationService locationService;
    private DeliveryPackageService deliveryPackageService;
    private int packageId;
    private final LocalDateTime now = LocalDateTime.now();
    Command command;


    @BeforeEach
    public void setUp(){
        PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
        customerService = new CustomerServiceImpl(persistenceManager);
        locationService = new LocationServiceImpl(persistenceManager);
        deliveryPackageService = new DeliveryPackageServiceImpl(persistenceManager, locationService);
        customerContactInfo = customerService.createCustomerContactInfo(VALID_NAME, VALID_PHONE, VALID_EMAIL, City.SYD);
        command = new GetPackageStateCommand(deliveryPackageService);
    }

    @Test
    public void execute_Should_Throw_When_InvalidParameterCount(){
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> command.execute(INVALID_PARAMETERS)
        );
    }

    @Test
    public void execute_Should_ReturnState_When_Valid(){
        deliveryPackageService.createDeliveryPackage(City.MEL, City.SYD, 20, customerContactInfo);
        String result = command.execute(List.of("1", "now"));
        Assertions.assertTrue(result.contains("Information for package id:"));
    }

    @Test
    public void parseParameters_Should_Throw_When_InvalidInteger(){
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> command.execute(List.of(INVALID_NUMBER, NOW_KEYWORD))
        );
    }

    @Test
    public void parseParameters_Should_Throw_When_InvalidDate(){
        Assertions.assertThrows(
                DateTimeException.class,
                () -> command.execute(List.of("1", INVALID_DATE))
        );
    }

}
