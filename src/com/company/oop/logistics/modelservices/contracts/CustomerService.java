package com.company.oop.logistics.modelservices.contracts;

import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;

public interface CustomerService {
    CustomerContactInfo getCustomerContactById(int customerContactInfoId);

    CustomerContactInfo createCustomerContactInfo(String fullName, String phoneNumber, String email, City address);
}
