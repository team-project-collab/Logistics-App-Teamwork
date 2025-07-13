package com.company.oop.logistics.tests.modelserivices;

import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.CustomerServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.util.ArrayList;
import java.util.List;

public class CustomerServiceImplTest {
    private CustomerServiceImpl customerService;
    private PersistenceManager persistenceManager;
    private static final String validName = "Name";
    private static final String validPhoneNumber = "0888123456";
    private static final String validEmail = "email@abv.bg";
    private static final City validCity = City.SYD;

    @BeforeEach
    public void setUp(){
        persistenceManager = new MockPersistenceManagerImpl();
        customerService = new CustomerServiceImpl(persistenceManager);
    }

    @Test
    public void createCustomerContactInfo_Should_AddNewCustomer() {
        customerService.createCustomerContactInfo(
                validName,
                validPhoneNumber,
                validEmail,
                validCity
        );
        Assertions.assertEquals(
                1,
                customerService.getAllCustomerContacts().size()
        );
    }

    @Test
    public void getCustomerContactById_Should_ReturnCorrectCustomer() {
        CustomerContactInfo customerContactInfo = customerService.createCustomerContactInfo(
                validName,
                validPhoneNumber,
                validEmail,
                validCity
        );
        Assertions.assertEquals(
                customerContactInfo.getFullName(),
                customerService.getCustomerContactById(customerContactInfo.getId()).getFullName()
        );
    }

    @Test
    public void getCustomerContactById_Should_ThrowException_When_CustomerDoesNotExist() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> customerService.getCustomerContactById(99)
        );
    }

    @Test
    public void getAllCustomerContacts_Should_ReturnAllCustomers_When_Called() {
        CustomerContactInfo customerContactInfo = customerService.createCustomerContactInfo(
                validName,
                validPhoneNumber,
                validEmail,
                validCity
        );
        Assertions.assertEquals(
                customerContactInfo.getId(),
                customerService.getAllCustomerContacts().get(0).getId()
        );
    }

    @Test
    public void getAllCustomerContacts_Should_ReturnCopy_When_Called() {
        CustomerContactInfo customerContactInfo = customerService.createCustomerContactInfo(
                validName,
                validPhoneNumber,
                validEmail,
                validCity
        );
        List<CustomerContactInfo> customerList = customerService.getAllCustomerContacts();
        customerList = new ArrayList<>();
        Assertions.assertEquals(
                customerContactInfo.getId(),
                customerService.getAllCustomerContacts().get(0).getId()
        );
    }

    @Test
    public void createCustomerContactInfo_Should_IncrementId_When_MultipleCustomersCreated() {
        CustomerContactInfo customerContactInfo1 = customerService.createCustomerContactInfo(
                validName,
                validPhoneNumber,
                validEmail,
                validCity
        );
        CustomerContactInfo customerContactInfo2 = customerService.createCustomerContactInfo(
                "Full name two",
                validPhoneNumber,
                validEmail,
                validCity
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        customerContactInfo1.getFullName(),
                        customerService.getCustomerContactById(1).getFullName()
                ),
                () -> Assertions.assertEquals(
                        customerContactInfo2.getFullName(),
                        customerService.getCustomerContactById(2).getFullName()
                )
        );
    }
}
