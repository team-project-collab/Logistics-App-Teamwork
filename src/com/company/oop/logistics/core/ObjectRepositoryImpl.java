package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.ObjectRepository;
import com.company.oop.logistics.models.LocationImpl;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ObjectRepositoryImpl implements ObjectRepository {
    private int nextId;

    List<Location> locations = new ArrayList<>();
    //TODO: Add lists for the objects we will store


    public ObjectRepositoryImpl() {
        nextId = 0;
    }

    public Location createLocation(City name, LocalDateTime arrivalTime, LocalDateTime departureTime){
        Location location = new LocationImpl(++nextId, name, arrivalTime, departureTime);
        this.locations.add(location);
        return location;
    }

    @Override
    public List<Location> getLocations() {
        return locations;
    }
}
