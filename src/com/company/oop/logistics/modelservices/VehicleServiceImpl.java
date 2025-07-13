package com.company.oop.logistics.modelservices;

import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.modelservices.contracts.VehicleService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.misc.InitializeTrucks;
import com.company.oop.logistics.utils.misc.LocationInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VehicleServiceImpl implements VehicleService {
    private static final String storagePath = "data/vehicles.xml";
    private static final String ERROR_NO_VEHICLE_ID = "There is no vehicle with id %s.";

    private final PersistenceManager persistenceManager;
    private final LocationService locationService;
    private final List<Truck> vehicles;


    public VehicleServiceImpl(PersistenceManager persistenceManager, LocationService locationService){
        this.persistenceManager = persistenceManager;
        this.locationService = locationService;
        vehicles = persistenceManager.loadData(storagePath);
    }

    public void initializeTrucks(){
        InitializeTrucks.execute(this);
    }

    private void save() {
        persistenceManager.saveData(vehicles, storagePath);
    }

    @Override
    public Truck createVehicle(String truckName, City initLocation) {
        Truck vehicle = new TruckImpl(truckName);
        Location location = locationService.createLocation(initLocation, LocalDateTime.now(), null);
        vehicle.addLocationIds(List.of(location.getId()));
        this.vehicles.add(vehicle);
        save();
        return vehicle;
    }

    public Truck getVehicleById(int vehicleId) {
        return vehicles.stream()
                .filter(t -> t.getId() == vehicleId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_NO_VEHICLE_ID, vehicleId)));
    }

    public List<Truck> getAllVehicles() {
        return new ArrayList<>(vehicles);
    }

    public void assignVehicle(int vehicleId, List<Integer> locationIds){
        Truck truck = getVehicleById(vehicleId);
        truck.addLocationIds(locationIds);
        save();
    }

    public boolean isVehicleFree(int vehicleId, LocalDateTime time){
        List<Integer> vehicleLocationIds = getVehicleById(vehicleId).getLocationIds();
        Location lastLocation = locationService.getLocationById(vehicleLocationIds.get(vehicleLocationIds.size() - 1));
        return time.isAfter(lastLocation.getArrivalTime());
    }

    public Location getCurrentLocation(int vehicleId, LocalDateTime time){
        LocationInfo locationInfo = new LocationInfo(locationService, getVehicleById(vehicleId).getLocationIds(), time);
        return locationInfo.getCurrentLocation();
    }
}
