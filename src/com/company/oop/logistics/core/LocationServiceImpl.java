package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.LocationService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.LocationImpl;
import com.company.oop.logistics.models.contracts.Identifiable;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.misc.LocationInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LocationServiceImpl implements LocationService {
    private final String storagePath = "data/locations.xml";
    private final PersistenceManager persistenceManager;
    public static final String ERROR_NO_LOCATION_ID = "No location with this id.";
    private final List<Location> locations;
    private int nextId;

    public LocationServiceImpl(PersistenceManager persistenceManager){
        this.persistenceManager = persistenceManager;
        locations = persistenceManager.loadData(storagePath);
        nextId = locations.stream().mapToInt(Identifiable::getId).max().orElse(0) + 1;
    }

    public void save() {
        persistenceManager.saveData(locations, storagePath);
    }

    @Override
    public Location createLocation(City name, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        Location createdLocation = new LocationImpl(nextId, name, arrivalTime, departureTime);
        nextId ++;
        locations.add(createdLocation);
        save();
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

    public List<Location> trimLocations(List<Location> entryList){
        List<Location> result = new ArrayList<>(entryList);
        if (entryList.size() > 1){
            Location start = entryList.get(0);
            Location end = entryList.get(entryList.size() - 1);
            entryList.set(0, createLocation(start.getName(), null, start.getDepartureTime()));
            entryList.set(entryList.size() - 1, createLocation(end.getName(), end.getArrivalTime(), null));
        }
        return result;
    }
}
