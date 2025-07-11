package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.DeliveryRoute;

import java.time.LocalDateTime;
import java.util.*;

public class DeliveryRouteImpl implements DeliveryRoute{
    public static final String ERROR_VEHICLE_ALREADY_ASSIGNED = "A vehicle is already assigned to route %d.";
    public static final String ERROR_START_TIME_NULL = "Start time cannot be null";
    public static final String ERROR_START_TIME_IN_THE_PAST = "Start time cannot be in the past.";

    private int id;
    private LocalDateTime startTime;
    private List<Integer> locationIds = new ArrayList<>();
    private final List<Integer> assignedPackageIds = new ArrayList<>();
    private int assignedVehicleId;
    private final int distance;

    public DeliveryRouteImpl(int id, LocalDateTime startTime, List<Integer> locationIds, int distance){
        this.distance = distance;
        setLocations(locationIds);
        setStartTime(startTime);
        setId(id);
    }

    public int getDistance() {
        return distance;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    private void setStartTime(LocalDateTime startTime) {
        if(startTime == null){
            throw new IllegalArgumentException(ERROR_START_TIME_NULL);
        }
        if(startTime.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException(ERROR_START_TIME_IN_THE_PAST);
        }
        this.startTime = startTime;
    }

    private void setId(int id) {
        this.id = id;
    }

    private void setLocations(List<Integer> locationIds){
        this.locationIds = new ArrayList<>(locationIds);
    }

    public int getId() {
        return id;
    }

    public List<Integer> getLocations() {
        return locationIds;
    }

    public int getAssignedVehicleId() {
        return assignedVehicleId;
    }

    @Override
    public int getOrigin() {
        return locationIds.get(0);
    }

    @Override
    public int getDestination() {
        return locationIds.get(locationIds.size()-1);
    }

    public ArrayList<Integer> getAssignedPackages() {
        return new ArrayList<>(assignedPackageIds);
    }

    public void addPackage(int packageId){
        assignedPackageIds.add(packageId);
    }

    @Override
    public void addLocation(int locationId) {
        locationIds.add(locationId);

    }

    @Override
    public void assignTruck(int truckId) {
        if (this.assignedVehicleId != 0){
            throw new IllegalArgumentException(String.format(ERROR_VEHICLE_ALREADY_ASSIGNED, truckId));
        }
        assignedVehicleId = truckId;
    }
}
