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
    private final PersistenceManager persistenceManager = new PersistenceManager();
    public static final String ERROR_NO_CUSTOMER_ID = "No customer contact with this id.";
    private int nextId;
    private List<CustomerContactInfo> customerContacts = new ArrayList<>();

    public CustomerServiceImpl() {
        load();
    }

    public void save() {
        persistenceManager.saveData(customerContacts, storagePath);
    }

    public void load() {
        List<CustomerContactInfo> loaded = persistenceManager.loadData(storagePath);
        if (loaded != null) {
            this.customerContacts = loaded;
        }
        nextId = customerContacts.stream().mapToInt(Identifiable::getId).max().orElse(0) + 1;
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
