package com.company.oop.logistics.modelservices;

import com.company.oop.logistics.modelservices.contracts.CustomerService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.misc.IdUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private static final String storagePath = "data/customerContacts.xml";
    private static final String ERROR_NO_CUSTOMER_ID = "No customer contact with this id.";

    private final PersistenceManager persistenceManager;
    private final List<CustomerContactInfo> customerContacts;
    private int nextId;

    public CustomerServiceImpl(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
        customerContacts = persistenceManager.loadData(storagePath);
        nextId = IdUtils.getNextId(customerContacts);
    }

    private void save() {
        persistenceManager.saveData(customerContacts, storagePath);
    }

    @Override
    public CustomerContactInfo createCustomerContactInfo(String fullName, String phoneNumber, String email, City address) {
        CustomerContactInfo createdCustomerContactInfo = new CustomerContactInfo(nextId, fullName, phoneNumber, email, address);
        nextId++;
        customerContacts.add(createdCustomerContactInfo);
        save();
        return createdCustomerContactInfo;
    }

    @Override
    public CustomerContactInfo getCustomerContactById(int customerContactInfoId) {
        return customerContacts.stream()
                .filter(cc -> cc.getId() == customerContactInfoId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ERROR_NO_CUSTOMER_ID));
    }

    @Override
    public List<CustomerContactInfo> getAllCustomerContacts(){
        return new ArrayList<>(customerContacts);
    }
}
