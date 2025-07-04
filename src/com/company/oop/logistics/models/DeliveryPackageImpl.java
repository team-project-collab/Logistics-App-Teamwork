package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.enums.City;

import java.util.ArrayList;

public class DeliveryPackageImpl implements DeliveryPackage {
    public static final String PACKAGE_TO_STRING = "Package id: %d; Origin: %s; Destination: %s;";
    private int id;
    private City startLocation;
    private City endLocation;
    private double weightKg;
    private int customerContactInfoId;
    private boolean isAssigned = false;
    private ArrayList<Integer> locationIds;

    public DeliveryPackageImpl(int id, City startLocation, City endLocation, double weightKg, int customerContactInfoId) {
        setId(id);
        setStartLocation(startLocation);
        setEndLocation(endLocation);
        setWeightKg(weightKg);
        setCustomerContactInfo(customerContactInfoId);
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    @Override
    public ArrayList<Integer> getLocations() {
        return locationIds;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public void setLocations(ArrayList<Integer> locationIds){
        this.locationIds = new ArrayList<>(locationIds);
    }

    private void setId(int id) {
        this.id = id;
    }

    public City getStartLocation() {
        return startLocation;
    }

    private void setStartLocation(City startLocation) {
        this.startLocation = startLocation;
    }

    public City getEndLocation() {
        return endLocation;
    }

   private void setEndLocation(City endLocation) {
        this.endLocation = endLocation;
    }

    public double getWeightKg() {
        return weightKg;
    }

    private void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    private void setCustomerContactInfo(int customerContactInfoId) {
        this.customerContactInfoId = customerContactInfoId;
    }

    public int getCustomerContactInfoId(){
        return this.customerContactInfoId;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public String toString(){
        return String.format(PACKAGE_TO_STRING, id, startLocation, endLocation);
    }
}
