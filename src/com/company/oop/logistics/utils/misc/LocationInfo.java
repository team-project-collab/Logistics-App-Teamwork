package com.company.oop.logistics.utils.misc;

import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.LocationType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class LocationInfo {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final String MESSAGE_VEHICLE_AWAITING_DEPARTURE = "Assigned at %s and ready to depart to %s at %s";
    public static final String MESSAGE_VEHICLE_FREE = "Free, stationed at %s";
    public static final String MESSAGE_VEHICLE_ONROUTE_TRAVELING = "On route, traveling from %s to %s. Expected arrival time: %s";
    public static final String MESSAGE_VEHICLE_ONROUTE_STATIONED = "On route, stationed at %s. Leaving to %s at %s.";
    public static final String MESSAGE_ROUTE_NOT_STARTED = "Route not started. Starting from %s at %s, next stop %s";
    public static final String MESSAGE_ROUTE_ONROUTE_TRAVELING = "Route in progress, traveling from %s to %s. Expected arrival time: %s";
    public static final String MESSAGE_ROUTE_ONROUTE_STATIONED = "Route in progress, stationed at %s. Leaving to %s at %s.";
    public static final String MESSAGE_ROUTE_FINISHED = "Route completed at %s";
    public static final String MESSAGE_PACKAGE_NOT_ASSIGNED = "Package is not yet assigned, stationed at %s";
    public static final String MESSAGE_PACKAGE_NOT_STARTED = "Package scheduled at %s, departing at %s, expected arrival to %s at %s";
    public static final String MESSAGE_PACKAGE_DELIVERED = "Package delivered, stationed at %s";
    public static final String MESSAGE_PACKAGE_TRAVELING = "Package in transit, expected arrival to %s at %s";
    public static final String MESSAGE_PACKAGE_STATIONARY = "Package stationary at %s. Departing at %s. Expected arrival to %s at %s";
    private final LocationService locationService;
    private final List<Integer> locationIds;
    private final LocalDateTime time;

    private Location currentLocation;
    private Location previousLocation;
    private Location nextLocation;
    private Location locationAfterNext;
    private Location lastLocation;


    public LocationInfo(LocationService locationService, List<Integer> locationIds, LocalDateTime time) {
        this.locationService = locationService;
        this.locationIds = locationIds;
        this.time = time;
        generateLocations();
    }

    public Location getPreviousLocation() {
        return previousLocation;
    }

    public Location getCurrentLocation(){
        return currentLocation;
    }

    public Location getNextLocation() {
        return nextLocation;
    }

    public Location getLocationAfterNext() {
        return locationAfterNext;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public String getTruckStatus(){
        String result = "";
        if (currentLocation.getType().equals(LocationType.END)) {
            if (nextLocation != null) {
                result = String.format(MESSAGE_VEHICLE_AWAITING_DEPARTURE,
                        currentLocation.getName(),
                        locationAfterNext.getName(),
                        nextLocation.getDepartureTime().format(formatter));
            }else{
                result = String.format(MESSAGE_VEHICLE_FREE, currentLocation.getName());
            }
        }
        if (currentLocation.getType().equals(LocationType.INTERMEDIATE)){
            if (currentLocation.getDepartureTime().isBefore(time)){
                result = String.format(MESSAGE_VEHICLE_ONROUTE_TRAVELING,
                        currentLocation.getName(),
                        nextLocation.getName(),
                        nextLocation.getArrivalTime().format(formatter));
            }else {
                result = String.format(MESSAGE_VEHICLE_ONROUTE_STATIONED,
                        currentLocation.getName(),
                        nextLocation.getName(),
                        currentLocation.getDepartureTime().format(formatter));
            }
        }
        if (currentLocation.getType().equals(LocationType.START)){
            result = String.format(MESSAGE_VEHICLE_ONROUTE_TRAVELING,
                    currentLocation.getName(),
                    nextLocation.getName(),
                    nextLocation.getArrivalTime().format(formatter));
        }
        return result;
    }

    public String getRouteStatus() {
        String result = "";
        if (currentLocation.getType().equals(LocationType.START)) {
            if (currentLocation.getDepartureTime().isAfter(time)) {
                result = String.format(MESSAGE_ROUTE_NOT_STARTED,
                        currentLocation.getName(),
                        currentLocation.getDepartureTime().format(formatter),
                        nextLocation.getName());
            }else{
                result = String.format(MESSAGE_ROUTE_ONROUTE_TRAVELING,
                        currentLocation.getName(),
                        nextLocation.getName(),
                        nextLocation.getArrivalTime().format(formatter));
            }
        }
        if (currentLocation.getType().equals(LocationType.INTERMEDIATE)) {
            if (currentLocation.getDepartureTime().isBefore(time)) {
                result = String.format(MESSAGE_ROUTE_ONROUTE_TRAVELING,
                        currentLocation.getName(),
                        nextLocation.getName(),
                        nextLocation.getArrivalTime().format(formatter));
            } else {
                result = String.format(MESSAGE_ROUTE_ONROUTE_STATIONED,
                        currentLocation.getName(),
                        nextLocation.getName(),
                        currentLocation.getDepartureTime().format(formatter));
            }
        }
        if (currentLocation.getType().equals(LocationType.END)) {
            result = String.format(MESSAGE_ROUTE_FINISHED, currentLocation.getName());
        }
        return result;
    }

    public String getPackageStatus() {
        String result = "";
        if (locationIds.size() == 1){
            return String.format(MESSAGE_PACKAGE_NOT_ASSIGNED, currentLocation.getName());
        }
        if (nextLocation == null){
            return String.format(MESSAGE_PACKAGE_DELIVERED, currentLocation.getName());
        }
        if (currentLocation.getType().equals(LocationType.END)) {
            return String.format(MESSAGE_PACKAGE_NOT_STARTED,
                    currentLocation.getName(),
                    nextLocation.getDepartureTime().format(formatter),
                    lastLocation.getName(),
                    lastLocation.getArrivalTime().format(formatter));
        }
        if (currentLocation.getType().equals(LocationType.INTERMEDIATE)) {
            if (currentLocation.getDepartureTime().isBefore(time)) {
                result = String.format(MESSAGE_PACKAGE_TRAVELING,
                        lastLocation.getName(),
                        lastLocation.getArrivalTime().format(formatter));
            } else {
                result = String.format(MESSAGE_PACKAGE_STATIONARY,
                        currentLocation.getName(),
                        currentLocation.getDepartureTime().format(formatter),
                        lastLocation.getName(),
                        lastLocation.getArrivalTime());
            }
        }
        if (currentLocation.getType().equals(LocationType.START)) {
            result = String.format(MESSAGE_PACKAGE_TRAVELING,
                    lastLocation.getName(),
                    lastLocation.getArrivalTime().format(formatter));
        }
        return result;
    }

    private void generateLocations(){
        if (locationIds == null || locationIds.isEmpty()){
            return;
        }
        List<Location> locations = locationIds.stream().map(locationService::getLocationById).toList();
        lastLocation = locations.get(locations.size() - 1);

        for (int i = locations.size() - 1; i > 0 ; i--) {
            if (isMatch(locations.get(i))){
                currentLocation = locations.get(i);
                previousLocation = locations.get(i - 1);
                if (i < locations.size() - 1){
                    nextLocation = locations.get(i + 1);
                    if (i < locations.size() - 2){
                        locationAfterNext = locations.get(i + 2);
                    }
                }
                break;
            }
        }
        if (currentLocation == null){
            currentLocation = locations.get(0);
            if (locations.size() > 1) {
                nextLocation = locations.get(1);
                if (locations.size() > 2){
                    locationAfterNext = locations.get(2);
                }
            }
        }
    }

    private boolean isMatch(Location location) {
        return switch (location.getType()) {
            case END -> location.getArrivalTime().isBefore(time);
            case START -> location.getDepartureTime().isBefore(time);
            case INTERMEDIATE -> location.getDepartureTime().isBefore(time) || location.getArrivalTime().isBefore(time);
        };
    }
}
