package com.company.oop.logistics.models.contracts;

import java.util.List;

public interface DeliveryRoute extends Identifiable{
    List<Integer> getLocations();
    void addLocation(int locationId);
    void assignTruck(int truckId);
    int getAssignedVehicleId();
    List<Integer> getAssignedPackages();
    int getOrigin();
    int getDestination();
    void addPackage(int packageId);
}
