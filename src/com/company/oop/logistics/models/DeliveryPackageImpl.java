package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.CustomerContactInfo;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.Location;

public class DeliveryPackageImpl implements DeliveryPackage {
    private int id;
    private Location startLocation;
    private Location endLocation;
    private double weightKg;
    private CustomerContactInfo customerContactInfo;

    public DeliveryPackageImpl(int id, Location startLocation, Location endLocation, double weightKg, CustomerContactInfo customerContactInfo) {
        setId(id);
        setStartLocation(startLocation);
        setEndLocation(endLocation);
        setWeightKg(weightKg);
        setCustomerContactInfo(customerContactInfo);
    }

    private void setId(int id) {
        this.id = id;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    private void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

   private void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public double getWeightKg() {
        return weightKg;
    }

    private void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public CustomerContactInfo getCustomerContactInfo() {
        return customerContactInfo;
    }

    private void setCustomerContactInfo(CustomerContactInfo customerContactInfo) {
        this.customerContactInfo = customerContactInfo;
    }

    @Override
    public int getId() {
        return this.id;
    }
}
