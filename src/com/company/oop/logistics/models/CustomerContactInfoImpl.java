package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;

public class CustomerContactInfoImpl implements CustomerContactInfo {
    private String fullName;
    private String phoneNumber;
    private City address;

    public CustomerContactInfoImpl(String fullName, String phoneNumber, City address) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }


    public String getFullName() {
        return fullName;
    }
    // TODO: Must implement validation.
    private void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    // TODO: Must implement validation.
    private void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public City getAddress() {
        return address;
    }
    // TODO: Must implement validation.
     private void setAddress(City address) {
        this.address = address;
    }
}
