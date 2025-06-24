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
    public static final String ERROR_CITIES_NOT_UNIQUE = "One route can visit the same city only once.";
    private int id;
    private LocalDateTime startTime;
    private ArrayList<Location> locations = new ArrayList<>();
    private Truck assignedVehicle;

    private final ArrayList<DeliveryPackage> assignedPackages = new ArrayList<>();

    public DeliveryRouteImpl(int id, LocalDateTime startTime, ArrayList<Location> locations){
        if (!hasUniqueCities(locations)){
            throw new IllegalArgumentException(ERROR_CITIES_NOT_UNIQUE);
        }
        setLocations(locations);
        setStartTime(startTime);
        setId(id);
    }

    private boolean hasUniqueCities(ArrayList<Location> locations){
        return locations.size() == locations.stream()
                .map(Location::getName)
                .distinct().count();
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
        ArrayList<Location> locationsToAdd =
                getLocations(deliveryPackage.getStartLocation(), deliveryPackage.getEndLocation());

        if((deliveryPackage.getWeightKg() +
                getMaxLoad(deliveryPackage.getStartLocation(), deliveryPackage.getEndLocation()))
                > assignedVehicle.getCapacity()){
            throw new LimitBreak("Exceeds capacity of truck");
        }
        deliveryPackage.setLocations(locationsToAdd);
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
        for (Location location : locations) {
            if (location.getName().equals(startLocation)) {
                withinSubroute = true;
            }
            if (location.getName().equals(endLocation)) {
                withinSubroute = false;
            }
            if (withinSubroute) {
                double weightSum = 0;
                for (DeliveryPackage assignedPackage : assignedPackages) {
                    ArrayList<Location> packageLocations = assignedPackage.getLocations();
                    packageLocations = new ArrayList<>(packageLocations.subList(0, packageLocations.size() - 1));
                    if (packageLocations.contains(location)) {
                        weightSum += assignedPackage.getWeightKg();
                    }
                }
                result.put(location.getName(), weightSum);
            }
        }
        return result;
    }

    private ArrayList<Location> getLocations(City startLocation, City endLocation){
        boolean withinStartEnd = false;
        ArrayList<Location> packageLocations = new ArrayList<>();
        for (Location location : locations) {
            if (location.getName() == startLocation) {
                withinStartEnd = true;
            }
            if (withinStartEnd) {
                packageLocations.add(location);
            }
            if (withinStartEnd && location.getName() == endLocation) {
                return packageLocations;
            }
        }
        throw new IllegalArgumentException("Route does not service this package");
    }

    public double getMaxLoad(City startLocation, City endLocation){
        return Collections.max(getLoad(startLocation, endLocation).entrySet(), Map.Entry.comparingByValue()).getValue();
    }



}
