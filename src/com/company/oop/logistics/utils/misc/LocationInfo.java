package com.company.oop.logistics.utils.misc;

import com.company.oop.logistics.core.contracts.LocationService;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.LocationType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class LocationInfo {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final LocationService locationService;
    private final List<Integer> locationIds;
    private final LocalDateTime time;

    private Location currentLocation;
    private Location previousLocation;
    private Location nextLocation;


    public LocationInfo(LocationService locationService, List<Integer> locationIds, LocalDateTime time) {
        this.locationService = locationService;
        this.locationIds = locationIds;
        this.time = time;
        generateLocations();
    }

    public String getTruckStatus(){
        String result = "";
        if (currentLocation.getType().equals(LocationType.START)) {
            if (nextLocation != null) {
                result = String.format("Assigned at %s and ready to depart to %s at %s",
                        currentLocation.getName(),
                        nextLocation.getName(),
                        currentLocation.getDepartureTime().format(formatter));
            }else{
                result = String.format("Free, stationed at %s", currentLocation.getName());
            }
        }
        if (currentLocation.getType().equals(LocationType.INTERMEDIATE)){
            if (currentLocation.getArrivalTime().isBefore(time)){
                result = String.format("On route, traveling from %s to %s. Expected arrival time: %s",
                        previousLocation.getName(),
                        currentLocation.getName(),
                        currentLocation.getArrivalTime().format(formatter));
            }else {
                result = String.format("On route, stationed at %s. Leaving to %s at %s.",
                        currentLocation.getName(),
                        currentLocation.getDepartureTime().format(formatter),
                        nextLocation.getName());
            }
        }
        if (currentLocation.getType().equals(LocationType.END)){
            result = String.format("Free, stationed at %s", currentLocation.getName());
        }
        return result;
    }

    public String getRouteStatus() {
        String result = "";
        if (currentLocation.getType().equals(LocationType.START)) {
            if (nextLocation != null) {
                result = String.format("Route not started. Starting at %s, next stop %s at %s",
                        currentLocation.getName(),
                        nextLocation.getName(),
                        currentLocation.getDepartureTime().format(formatter));
            }
        }
        if (currentLocation.getType().equals(LocationType.INTERMEDIATE)) {
            if (currentLocation.getArrivalTime().isBefore(time)) {
                result = String.format("Route in progress, traveling to %s. Expected arrival time: %s",
                        currentLocation.getName(),
                        currentLocation.getArrivalTime().format(formatter));
            } else {
                result = String.format("Route in progress, stationed at %s. Leaving to %s at %s.",
                        currentLocation.getName(),
                        currentLocation.getDepartureTime().format(formatter),
                        nextLocation.getName());
            }
        }
        if (currentLocation.getType().equals(LocationType.END)) {
            result = String.format("Route completed at %s", currentLocation.getName());
        }
        return result;
    }

    public String getPackageStatus() {
        if (currentLocation == null){
            return "Package is not yet assigned.";
        }
        String result = "";
        if (currentLocation.getType().equals(LocationType.START)) {
            if (nextLocation != null) {
                result = String.format("Package scheduled at %s, next stop %s, departing at %s",
                        currentLocation.getName(),
                        nextLocation.getName(),
                        currentLocation.getDepartureTime().format(formatter));
            }
        }
        if (currentLocation.getType().equals(LocationType.INTERMEDIATE)) {
            if (currentLocation.getArrivalTime().isBefore(time)) {
                result = String.format("Package in transit, traveling to %s. Expected arrival time: %s",
                        currentLocation.getName(),
                        currentLocation.getArrivalTime().format(formatter));
            } else {
                result = String.format("Package stationary at %s. Leaving to %s at %s.",
                        currentLocation.getName(),
                        currentLocation.getDepartureTime().format(formatter),
                        nextLocation.getName());
            }
        }

        if (currentLocation.getType().equals(LocationType.END)) {
            result = String.format("Package delivered, stationed at %s", currentLocation.getName());
        }
        return result;
    }

    private void generateLocations(){
        if (locationIds == null || locationIds.isEmpty()){
            return;
        }
        List<Location> locations = locationIds.stream().map(locationService::getLocationById).toList();
        currentLocation = locations.get(0);
        if (locations.size() > 1) {
            nextLocation = locations.get(1);
        }
        for (int i = locations.size() - 1; i > 0 ; i--) {
            if (isMatch(locations.get(i))){
                currentLocation = locations.get(i);
                previousLocation = locations.get(i - 1);
                if (i < locations.size() - 1){
                    nextLocation = locations.get(i + 1);
                }
                break;
            }
        }
    }

    private boolean isMatch(Location location) {
        return switch (location.getType()) {
            case END -> location.getArrivalTime().isBefore(time);
            case START -> location.getDepartureTime().isBefore(time);
            case INTERMEDIATE ->
                    location.getDepartureTime().isBefore(time) || location.getArrivalTime().isBefore(time);
        };
    }
}
