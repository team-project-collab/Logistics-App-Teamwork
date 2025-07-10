package com.company.oop.logistics.modelservices.contracts;

import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;

import java.util.List;

public interface CustomerService {
    CustomerContactInfo createCustomerContactInfo(String fullName, String phoneNumber, String email, City address);

    CustomerContactInfo getCustomerContactById(int customerContactInfoId);

    List<CustomerContactInfo> getAllCustomerContacts();
}
