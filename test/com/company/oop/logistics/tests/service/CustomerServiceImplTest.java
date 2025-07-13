package com.company.oop.logistics.tests.service;

import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerServiceImplTest {

    private PersistenceManager persistenceManager;
    private CustomerServiceImplTest customerService;

    @BeforeEach
    public void setUp() {
        persistenceManager = Mockito.mock(PersistenceManager.class);
        Mockito.when(persistenceManager.loadData(Mockito.anyString())).thenReturn(new ArrayList<>());
        customerService = new CustomerServiceImplTest(persistenceManager);
    }

    @Test
    public void createCustomerContactInfo_ShouldCreateAndSaveCustomer() {
        String fullName = "John Doe";
        String phone = "123456789";
        String email = "john@example.com";
        City city = City.ADL;

        CustomerContactInfo customer = customerService.createCustomerContactInfo(fullName, phone, email, city);

        assertNotNull(customer);
        assertEquals(fullName, customer.getFullName());
        assertEquals(phone, customer.getPhoneNumber());
        assertEquals(email, customer.getEmail());
        assertEquals(city, customer.getAddress());

        Mockito.verify(persistenceManager, Mockito.times(1))
                .saveData(Mockito.anyList(), Mockito.eq("data/customerContacts.xml"));
    }

    @Test
    public void getCustomerContactById_ShouldReturnCustomer_WhenFound() {
        // Prepare a customer to simulate loaded data
        CustomerContactInfo customerMock = Mockito.mock(CustomerContactInfo.class);
        Mockito.when(customerMock.getId()).thenReturn(1);

        List<CustomerContactInfo> customers = new ArrayList<>();
        customers.add(customerMock);

        Mockito.when(persistenceManager.loadData(Mockito.anyString())).thenReturn(customers);

        // Reinitialize service to pick up new data
        customerService = new CustomerServiceImplTest(persistenceManager);

        CustomerContactInfo foundCustomer = customerService.getCustomerContactById(1);
        assertEquals(customerMock, foundCustomer);
    }

    @Test
    public void getCustomerContactById_ShouldThrow_WhenNotFound() {
        assertThrows(IllegalArgumentException.class, () -> customerService.getCustomerContactById(999));
    }

    @Test
    public void getAllCustomerContacts_ShouldReturnAllCustomers() {
        CustomerContactInfo customer1 = Mockito.mock(CustomerContactInfo.class);
        CustomerContactInfo customer2 = Mockito.mock(CustomerContactInfo.class);

        List<CustomerContactInfo> customers = List.of(customer1, customer2);
        Mockito.when(persistenceManager.loadData(Mockito.anyString())).thenReturn(customers);

        customerService = new CustomerServiceImplTest(persistenceManager);

        List<CustomerContactInfo> allCustomers = customerService.getAllCustomerContacts();
        assertEquals(2, allCustomers.size());
        assertTrue(allCustomers.contains(customer1));
        assertTrue(allCustomers.contains(customer2));
    }

}
