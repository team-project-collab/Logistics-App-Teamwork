package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;
// This is a test.
public class CustomerContactInfoImpl implements CustomerContactInfo {
    private String fullName;
    private String phoneNumber;
    private String email;
    private City address;

    public CustomerContactInfoImpl(String fullName, String phoneNumber, String email, City address) {
        setFullName(fullName);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setAddress(address);
    }

    public String getEmail() {
        return email;
    }
    // TODO: Must implement validation.
    private void setEmail(String email) {
        this.email = email;
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
