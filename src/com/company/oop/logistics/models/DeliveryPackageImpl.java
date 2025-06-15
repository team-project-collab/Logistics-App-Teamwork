package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DeliveryPackageImpl implements DeliveryPackage {
    private int id;
    private City startLocation;
    private City endLocation;
    private double weightKg;
    private CustomerContactInfo customerContactInfo;
    private boolean isAssigned = false;
    private ArrayList<Location> locations;

    public boolean isAssigned() {
        return isAssigned;
    }

    @Override
    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public DeliveryPackageImpl(int id, City startLocation, City endLocation, double weightKg, CustomerContactInfo customerContactInfo) {
        setId(id);
        setStartLocation(startLocation);
        setEndLocation(endLocation);
        setWeightKg(weightKg);
        setCustomerContactInfo(customerContactInfo);
    }


    public void setLocations(ArrayList<Location> locations){
        this.locations = new ArrayList<>(locations);
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

    public String getState(LocalDateTime time){
        boolean packageSent = false;
        boolean packageInTransit = false;
        City travelingTo = null;
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getArrivalTime().isAfter(time) && !packageSent){
                break;
            }
            if (locations.get(i).getArrivalTime().isBefore(time)){
                packageSent = true;
            }
            if (locations.get(i).getArrivalTime().isAfter(time) && packageSent){
                packageInTransit = true;
                travelingTo = locations.get(i).getName();
                break;
            }
        }
        if (packageInTransit){
            return String.format("Package is in transit. Currently traveling to: %s", travelingTo);
        }
        if (packageSent && !packageInTransit){
            return String.format("Package is delivered to %s", locations.get(locations.size() - 1).getName());
        }
        return "Package is not yet assigned";
    }
}
