package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.VehicleService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.contracts.Truck;

import java.util.ArrayList;
import java.util.List;

public class VehicleServiceImpl implements VehicleService {
    private final String storagePath = "data/vehicles.xml";
    private final PersistenceManager persistenceManager = new PersistenceManager();
    public static final String ERROR_NO_VEHICLE_ID = "There is no vehicle with id %s.";
    List<Truck> vehicles = new ArrayList<>();

    public VehicleServiceImpl(){
        load();
    }
    @Override
    public Truck createVehicle(String truckName) {
        Truck vehicle = new TruckImpl(truckName);
        this.vehicles.add(vehicle);
        save();
        return vehicle;
    }

    public void save() {
        persistenceManager.saveData(vehicles, storagePath);
    }

    public void load() {
        List<Truck> loaded = persistenceManager.loadData(storagePath);
        if (loaded != null) {
            this.vehicles = loaded;
        }
    }

    public Truck getVehicleById(int vehicleId) {
        return vehicles.stream()
                .filter(v -> v.getId() == vehicleId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_NO_VEHICLE_ID, vehicleId)));
    }

    public List<Truck> getVehicles() {
        return this.vehicles;
    }
}
