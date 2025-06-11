package com.company.oop.logistics.models.contracts;

import com.company.oop.logistics.models.enums.City;

public interface CustomerContactInfo {
    String getFullName();
    String getPhoneNumber();
    City getAddress();
    String getEmail();

}
