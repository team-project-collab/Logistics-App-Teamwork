package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.CustomerService;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;

import java.util.ArrayList;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    public static final String ERROR_NO_CUSTOMER_ID = "No customer contact with this id.";
    private int nextId;
    private List<CustomerContactInfo> customerContacts = new ArrayList<>();

    public CustomerServiceImpl() {
        nextId = 0;
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
        CustomerContactInfo createdCustomerContactInfo = new CustomerContactInfo(++nextId, fullName, phoneNumber, email, address);
        customerContacts.add(createdCustomerContactInfo);
        return createdCustomerContactInfo;
    }
}
