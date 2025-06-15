package com.company.oop.logistics.models.contracts;

import com.company.oop.logistics.models.enums.City;

import java.util.ArrayList;
import java.util.HashMap;

public interface DeliveryRoute extends Identifiable{
    ArrayList<Location> getLocations();
    void addLocation(Location location);
    void assignTruck(Truck truck);
    void assignPackage(DeliveryPackage deliveryPackage);
    Truck getAssignedVehicle();
    ArrayList<DeliveryPackage> assignedPackages();
    Location getOrigin();
    Location getDestination();
    int getDistance();
    HashMap <City, Double> getLoad(City startLocation, City endLocation);
    double getMaxLoad(City startLocation, City endLocation);
}
