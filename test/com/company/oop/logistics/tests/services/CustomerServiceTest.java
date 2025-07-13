package com.company.oop.logistics.tests.services;

import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockPersistenceManagerImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerServiceTest {
    private PersistenceManager persistenceManager;
    private CustomerServiceImpl customerService;

    @BeforeEach
    public void setUp() {
        persistenceManager = new MockPersistenceManagerImpl();
        customerService = new CustomerServiceImpl(persistenceManager);
    }

    @Test
    public void createCustomerContactInfo_Should_CreateAndSaveCustomer() {
        CustomerContactInfo customer = customerService.createCustomerContactInfo(
                "John Doe",
                "123456789",
                "john@example.com",
                City.ADL
        );

        assertNotNull(customer);
        assertEquals("John Doe", customer.getFullName());
        assertEquals("123456789", customer.getPhoneNumber());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals(City.ADL, customer.getAddress());

        CustomerContactInfo retrieved = customerService.getCustomerContactById(customer.getId());
        assertEquals(customer, retrieved);
    }

    @Test
    public void getCustomerContactById_Should_ReturnCustomer_WhenFound() {

        CustomerContactInfo created = customerService.createCustomerContactInfo(
                "Jane Doe",
                "987654321",
                "jane@example.com",
                City.MEL
        );

        CustomerContactInfo found = customerService.getCustomerContactById(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Jane Doe", found.getFullName());
    }

    @Test
    public void getCustomerContactById_Should_ThrowException_WhenNotFound() {
        assertThrows(IllegalArgumentException.class, () -> customerService.getCustomerContactById(9999));
    }

    @Test
    public void getAllCustomerContacts_Should_ReturnAllCreatedCustomers() {
        CustomerContactInfo c1 = customerService.createCustomerContactInfo("Alice", "111111111", "alice@example.com", City.SYD);
        CustomerContactInfo c2 = customerService.createCustomerContactInfo("Bob", "222222222", "bob@example.com", City.PER);

        List<CustomerContactInfo> allCustomers = customerService.getAllCustomerContacts();

        assertEquals(2, allCustomers.size());
        assertTrue(allCustomers.contains(c1));
        assertTrue(allCustomers.contains(c2));
    }


}
