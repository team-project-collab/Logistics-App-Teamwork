package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.Identifiable;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.validation.ValidationHelpers;


public class CustomerContactInfo implements Identifiable {
    public static final String ERROR_CITY_IS_NULL = "Address (City) cannot be null.";
    private int id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private City address;

    public CustomerContactInfo(int id, String fullName, String phoneNumber, String email, City address) {
        setId(id);
        setFullName(fullName);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setAddress(address);
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) {
        this.id = id;
    }

    private void setEmail(String email) {
        ValidationHelpers.validateEmail(email);
        this.email = email.trim();
    }

    public String getFullName() {
        return fullName;
    }

    private void setFullName(String fullName) {
        ValidationHelpers.validateName(fullName);
        this.fullName = fullName.trim();
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }


    private void setPhoneNumber(String phoneNumber) {
        ValidationHelpers.validatePhoneNumber(phoneNumber);
        this.phoneNumber = phoneNumber.trim();
    }


    public City getAddress() {
        return address;
    }


    private void setAddress(City address) {
        if (address == null) {
            throw new IllegalArgumentException(ERROR_CITY_IS_NULL);
        }
        this.address = address;
    }


    @Override
    public int getId() {
        return this.id;
    }
}
