package com.company.oop.logistics.modelservices.contracts;

import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.List;

public interface LocationService {
    Location createLocation(City name, LocalDateTime arrivalTime, LocalDateTime departureTime);

    List<Location> getLocations();

    Location getLocationById(int startLocationId);

    List<Location> trimLocations(List<Location> entryList);
}
