package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.models.enums.PackageStatus;

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

    public PackageStatus getPackageStatus(LocalDateTime time) {
        if (locations == null || locations.isEmpty()) {
            return PackageStatus.NOT_ASSIGNED;
        }
        if (time.isBefore(locations.get(0).getDepartureTime())) {
            return PackageStatus.SCHEDULED;
        }
        for (Location location : locations) {
            if (time.isBefore(location.getDepartureTime())) {
                if (time.isAfter(location.getArrivalTime())) {
                    return PackageStatus.STATIONARY;
                } else {
                    return PackageStatus.IN_TRANSIT;
                }
            }
        }
        Location last = locations.get(locations.size() - 1);
        if (time.isAfter(last.getArrivalTime())) {
            return PackageStatus.DELIVERED;
        }
        throw new RuntimeException("Error getting package status");
    }

    public String getPackageStatusDescription(LocalDateTime time) {
        PackageStatus state = getPackageStatus(time);
        switch (state) {
            case NOT_ASSIGNED:
                return "Package is not yet assigned";
            case SCHEDULED:
                return "Package is scheduled but has not yet entered transit";
            case STATIONARY:
                return String.format("Package is currently stationary in %s", getCurrentLocation(time).getName());
            case IN_TRANSIT:
                return String.format("Package is in transit. Currently traveling to: %s", getNextLocation(time).getName());
            case DELIVERED:
                return String.format("Package is delivered to %s", getLastLocation().getName());
            default:
                return "Unknown state";
        }
    }

    private Location getCurrentLocation(LocalDateTime time){
        return locations.stream()
                .filter(l -> l.getArrivalTime().isBefore(time) && l.getDepartureTime().isAfter(time))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Illegal method call for package that is not stationary"));
    }

    private Location getNextLocation(LocalDateTime time){
        return locations.stream()
                .filter(l -> l.getDepartureTime().isAfter(time))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Illegal method call for package that is not in transit"));
    }

    private Location getLastLocation(){
        return locations.get(locations.size() - 1);
    }

    public String toString(){
        return String.format("Package id: %d; Origin: %s; Destination: %s;", id, startLocation,
                endLocation);
    }
}
