package com.company.oop.logistics.models;

import com.company.oop.logistics.exceptions.custom.LimitBreak;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.constants.CityDistance;

import java.time.LocalDateTime;
import java.util.*;

public class DeliveryRouteImpl implements DeliveryRoute{
    public static final String ERROR_VEHICLE_ALREADY_ASSIGNED = "A vehicle is already assigned to route %d.";
    public static final String ERROR_START_TIME_NULL = "Start time cannot be null";
    public static final String ERROR_START_TIME_IN_THE_PAST = "Start time cannot be in the past.";
    public static final String ERROR_NO_VEHICLE = "Route %d has no vehicle yet.";
    private int id;
    private LocalDateTime startTime;
    private ArrayList<Location> locations = new ArrayList<>();
    private Truck assignedVehicle;

    private final ArrayList<DeliveryPackage> assignedPackages = new ArrayList<>();

    public DeliveryRouteImpl(int id, LocalDateTime startTime, ArrayList<Location> locations){
        //TODO: Validate: Route cannot have the same city twice
        setLocations(locations);
        setStartTime(startTime);
        setId(id);
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
    public ArrayList<DeliveryPackage> assignedPackages() {
        return new ArrayList<>(assignedPackages);
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
        if (truck == null) {
            throw new IllegalArgumentException("Truck cannot be null");
        }

        if (this.assignedVehicle != null){
            throw new IllegalArgumentException(String.format(ERROR_VEHICLE_ALREADY_ASSIGNED, id));
        }

        assignedVehicle = truck;
    }

    @Override
    public void assignPackage(DeliveryPackage deliveryPackage) {
        if (this.assignedVehicle == null){
            throw new IllegalStateException(String.format(ERROR_NO_VEHICLE, id));
        }
        if((deliveryPackage.getWeightKg() + getTotalLoad()) > assignedVehicle.getCapacity()){
            throw new LimitBreak("Exceeds capacity of truck");
        }
        boolean withinStartEnd = false;
        ArrayList<Location> packageLocations = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getName() == deliveryPackage.getStartLocation()){
                withinStartEnd = true;
            }
            if (withinStartEnd) {
                packageLocations.add(locations.get(i));
            }
            if (locations.get(i).getName() == deliveryPackage.getEndLocation()){
                break;
            }
        }
        deliveryPackage.setLocations(packageLocations);
        assignedPackages.add(deliveryPackage);
    }

    public double getTotalLoad(){
        double total = 0;
        for (DeliveryPackage deliveryPackage:
        getAssignedPackages()) {
            total += deliveryPackage.getWeightKg();
        }
        return total;
    }

    public int getDistance(){
        int result = 0;
        for (int i = 0; i < locations.size() - 1; i++){
            result += CityDistance.getDistance(locations.get(i).getName(), locations.get(i + 1).getName());
        }
        return result;
    }

    public HashMap <City, Double> getLoad(City startLocation, City endLocation){
        boolean withinSubroute = false;
        HashMap <City, Double> result = new HashMap<>();
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getName().equals(startLocation)) {
                withinSubroute = true;
            }
            if (locations.get(i).getName().equals(endLocation)) {
                withinSubroute = false;
            }
            if (withinSubroute) {
                double weightSum = 0;
                for (int j = 0; j < assignedPackages.size(); j++) {
                    if (assignedPackages.get(j).getLocations().contains(locations.get(i))) {
                        weightSum += assignedPackages.get(j).getWeightKg();
                    }
                }
                result.put(locations.get(i).getName(), weightSum);
            }
        }
        return result;
    }


    public double getMaxLoad(City startLocation, City endLocation){
        return Collections.max(getLoad(startLocation, endLocation).entrySet(), Map.Entry.comparingByValue()).getValue();
    }

}
