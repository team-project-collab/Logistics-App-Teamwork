package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.VehicleService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.contracts.Identifiable;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.TruckName;

import java.util.ArrayList;
import java.util.List;

public class VehicleServiceImpl implements VehicleService {
    private final String storagePath = "data/vehicles.xml";
    private final PersistenceManager persistenceManager;
    public static final String ERROR_NO_VEHICLE_ID = "There is no vehicle with id %s.";
    private List<Truck> vehicles;

    public VehicleServiceImpl(PersistenceManager persistenceManager){
        this.persistenceManager = persistenceManager;
        vehicles = persistenceManager.loadData(storagePath);
        setIds();
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

    public void setIds() {
        List<Truck> loaded = persistenceManager.loadData(storagePath);
        if (loaded != null) {
            this.vehicles = loaded;
        }
        int maxScaniaId = vehicles.stream().
                filter(t -> t.getTruckName().equals(TruckName.SCANIA)).
                mapToInt(Identifiable::getId).max().orElse(1000) + 1;
        int maxManId = vehicles.stream().
                filter(t -> t.getTruckName().equals(TruckName.MAN))
                .mapToInt(Identifiable::getId).max().orElse(1010) + 1;
        int maxActrosId = vehicles.stream().
                filter(t -> t.getTruckName().equals(TruckName.ACTROS)).
                mapToInt(Identifiable::getId).max().orElse(1025) + 1;

        TruckImpl.setIds(maxScaniaId, maxManId, maxActrosId);

    }

    public Truck getVehicleById(int vehicleId) {
        for (Truck vehicle: this.vehicles){
            if (vehicle.getId() == vehicleId){
                return vehicle;
            }
        }
        throw new IllegalArgumentException(String.format(ERROR_NO_VEHICLE_ID, vehicleId));
        /*return vehicles.stream()
                .filter(t -> t.getId() == vehicleId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_NO_VEHICLE_ID, vehicleId)));
                */
    }

    public List<Truck> getVehicles() {
        return this.vehicles;
    }

    public void assignVehicle(int vehicleId, List<Integer> locationIds){
        Truck truck = getVehicleById(vehicleId);
        truck.addLocationIds(locationIds);
        save();
    }
}
