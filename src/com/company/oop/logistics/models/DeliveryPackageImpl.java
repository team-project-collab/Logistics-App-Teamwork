package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.enums.City;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPackageImpl implements DeliveryPackage {
    public static final String PACKAGE_TO_STRING = "Package id: %d; Origin: %s; Destination: %s; Weight: %.1f kg";

    private int id;
    private City startLocation;
    private City endLocation;
    private double weightKg;
    private int customerContactInfoId;
    private boolean isAssigned = false;
    private final List<Integer> locationIds = new ArrayList<>();
    private int assignedRoute;

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
    public List<Integer> getLocations() {
        return new ArrayList<>(locationIds);
    }

    public void setLocations(List<Integer> locationIds){
        this.locationIds.addAll(locationIds);
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
        return String.format(PACKAGE_TO_STRING, id, startLocation, endLocation, weightKg);
    }

    public int getAssignedRoute() {
        return assignedRoute;
    }

    public void assign(int routeId){
        isAssigned = true;
        assignedRoute = routeId;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }
}
