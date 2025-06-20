package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.LocationService;
import com.company.oop.logistics.models.LocationImpl;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LocationServiceImpl implements LocationService {
    public static final String ERROR_NO_LOCATION_ID = "No location with this id.";
    List<Location> locations = new ArrayList<>();


    @Override
    public Location createLocation(City name, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        Location createdLocation = new LocationImpl(name, arrivalTime, departureTime);
        locations.add(createdLocation);
        return createdLocation;
    }

    @Override
    public List<Location> getLocations() {
        return locations;
    }

    @Override
    public Location getLocationById(int locationId) {
        return locations.stream()
                .filter(l -> l.getId() == locationId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ERROR_NO_LOCATION_ID));
    }
}
