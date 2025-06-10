package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DeliveryRouteImpl implements DeliveryRoute{
    private int id;
    private LocalDateTime startTime;
    private ArrayList<Location> locations = new ArrayList<>();
    private Truck assignedVehicle;
    private final ArrayList<DeliveryPackage> assignedPackages = new ArrayList<>();

    public DeliveryRouteImpl(int id, LocalDateTime startTime, ArrayList<Location> locations){
        setLocations(locations);
        setStartTime(startTime);
        setId(id);
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }

    private void setStartTime(LocalDateTime startTime) {
        if(startTime == null){
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if(startTime.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Start time cannot be in the past.");
        }
        this.startTime = startTime;
    }

    private void setId(int id) {
        this.id = id;
    }

    private void setLocations(ArrayList<Location> locations){
        this.locations = new ArrayList<>(locations);
    }

    private void setAssignedVehicle(Truck assignedVehicle) {
        this.assignedVehicle = assignedVehicle;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public Truck getAssignedVehicle() {
        return assignedVehicle;
    }

    @Override
    public DeliveryPackage assignedPackages() {
        return null;
    }

    @Override
    public Location getOrigin() {
        return locations.get(0);
    }

    @Override
    public Location getDestination() {
        return locations.get(locations.size()-1);
    }

    public ArrayList<DeliveryPackage> getAssignedPackages() {
        return assignedPackages;
    }

    @Override
    public void addLocation(Location location) {
        locations.add(location);

    }

    @Override
    public void assignTruck(Truck truck) {

    }

    @Override
    public void assignPackage(DeliveryPackage deliveryPackage) {

    }

}
