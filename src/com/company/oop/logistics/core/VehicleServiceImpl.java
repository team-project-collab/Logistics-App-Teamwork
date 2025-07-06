package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.LocationService;
import com.company.oop.logistics.core.contracts.VehicleService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.misc.InitializeTrucks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VehicleServiceImpl implements VehicleService {
    private final String storagePath = "data/vehicles.xml";
    private final PersistenceManager persistenceManager;
    private final LocationService locationService;
    public static final String ERROR_NO_VEHICLE_ID = "There is no vehicle with id %s.";
    private final List<Truck> vehicles;

    public VehicleServiceImpl(PersistenceManager persistenceManager, LocationService locationService){
        this.persistenceManager = persistenceManager;
        this.locationService = locationService;
        vehicles = persistenceManager.loadData(storagePath);
        InitializeTrucks.execute(this);
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

    public void save() {
        persistenceManager.saveData(vehicles, storagePath);
    }



    public Truck getVehicleById(int vehicleId) {
        return vehicles.stream()
                .filter(t -> t.getId() == vehicleId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_NO_VEHICLE_ID, vehicleId)));
    }

    public List<Truck> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    public void assignVehicle(int vehicleId, List<Integer> locationIds){
        Truck truck = getVehicleById(vehicleId);
        truck.addLocationIds(locationIds);
        save();
    }
}
