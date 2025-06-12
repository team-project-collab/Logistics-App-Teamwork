package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.Identifiable;
import com.company.oop.logistics.models.enums.City;


public class CustomerContactInfo implements Identifiable {
    private int id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private City address;

    public CustomerContactInfo(int id, String fullName, String phoneNumber, String email, City address) {
        this.id = id;
        setFullName(fullName);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setAddress(address);
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        if (email == null || !email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email address provided: " + email);
        }
        this.email = email.trim();
    }

    public String getFullName() {
        return fullName;
    }

    private void setFullName(String fullName) {

        String nameRegex = "^[A-Za-z][A-Za-z\\s'-]*$";

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty.");
        }

        if (!fullName.matches(nameRegex)) {
            throw new IllegalArgumentException("Invalid full name format.");
        }

        this.fullName = fullName.trim();
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }


    private void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty.");
        }

        String phoneRegex = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]{6,}$";
        if (!phoneNumber.matches(phoneRegex)) {
            throw new IllegalArgumentException("Invalid phone number format.");
        }

        this.phoneNumber = phoneNumber.trim();
    }


    public City getAddress() {
        return address;
    }


    private void setAddress(City address) {
        if (address == null) {
            throw new IllegalArgumentException("Address (City) cannot be null.");
        }
        this.address = address;
    }


    @Override
    public int getId() {
        return this.id;
    }
}
