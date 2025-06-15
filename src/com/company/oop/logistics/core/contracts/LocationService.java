package com.company.oop.logistics.core.contracts;

import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.List;

public interface LocationService {
    Location createLocation(City name, LocalDateTime arrivalTime, LocalDateTime departureTime);

    List<Location> getLocations();

    Location getLocationById(int startLocationId);
}
