package testingUtils;

import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.utils.misc.IdUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockLocationServiceImpl implements LocationService {
    private static final String storagePath = "data/locations.xml";
    private static final String ERROR_NO_LOCATION_ID = "No location with this id.";

    private final PersistenceManager persistenceManager;
    private final List<Location> locations;
    private int nextId;

    public MockLocationServiceImpl(PersistenceManager persistenceManager){
        this.persistenceManager = persistenceManager;
        locations = persistenceManager.loadData(storagePath);
        nextId = IdUtils.getNextId(locations);
    }

    private void save() {
        persistenceManager.saveData(locations, storagePath);
    }

    @Override
    public Location createLocation(City name, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        Location createdLocation = new MockLocationImpl(nextId, name, arrivalTime, departureTime);
        nextId ++;
        locations.add(createdLocation);
        save();
        return createdLocation;
    }

    @Override
    public Location getLocationById(int locationId) {
        return locations.stream()
                .filter(l -> l.getId() == locationId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ERROR_NO_LOCATION_ID));
    }

    @Override
    public List<Location> getAllLocations() {
        return new ArrayList<>(locations);
    }

    //Trim locations to remove arrivalTime from the start and departureTime from the end
    @Override
    public List<Location> trimLocations(List<Location> entryList){
        List<Location> result = new ArrayList<>(entryList);
        if (result.size() > 1){
            Location start = result.get(0);
            Location end = result.get(result.size() - 1);
            result.set(0, createLocation(start.getName(), null, start.getDepartureTime()));
            result.set(result.size() - 1, createLocation(end.getName(), end.getArrivalTime(), null));
        }
        return result;
    }
}
