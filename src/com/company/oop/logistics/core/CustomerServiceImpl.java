package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.CustomerService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.contracts.Identifiable;
import com.company.oop.logistics.models.enums.City;

import java.util.ArrayList;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private final String storagePath = "data/customerContacts.xml";
    private final PersistenceManager persistenceManager;
    public static final String ERROR_NO_CUSTOMER_ID = "No customer contact with this id.";
    private int nextId;
    private final List<CustomerContactInfo> customerContacts;

    public CustomerServiceImpl(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
        customerContacts = persistenceManager.loadData(storagePath);
        nextId = customerContacts.stream().mapToInt(Identifiable::getId).max().orElse(0) + 1;
    }

    public void save() {
        persistenceManager.saveData(customerContacts, storagePath);
    }

    @Override
    public CustomerContactInfo getCustomerContactById(int customerContactInfoId) {
        for (CustomerContactInfo contact : customerContacts) {
            if (contact.getId() == customerContactInfoId) {
                return contact;
            }
        }
        throw new IllegalArgumentException(ERROR_NO_CUSTOMER_ID);
    }

    @Override
    public CustomerContactInfo createCustomerContactInfo(String fullName, String phoneNumber, String email, City address) {
        CustomerContactInfo createdCustomerContactInfo = new CustomerContactInfo(nextId, fullName, phoneNumber, email, address);
        nextId++;
        customerContacts.add(createdCustomerContactInfo);
        save();
        return createdCustomerContactInfo;
    }
}
