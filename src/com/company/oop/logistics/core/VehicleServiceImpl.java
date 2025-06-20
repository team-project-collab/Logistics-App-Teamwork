package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.VehicleService;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Truck;

import java.util.ArrayList;
import java.util.List;

public class VehicleServiceImpl implements VehicleService {
    public static final String ERROR_NO_VEHICLE_ID = "There is no vehicle with id %s.";
    List<Truck> vehicles = new ArrayList<>();


    @Override
    public Truck createVehicle(String truckName) {
        Truck vehicle = new TruckImpl(truckName);
        this.vehicles.add(vehicle);
        return vehicle;
    }

    public Truck getVehicleById(int vehicleId) {
        return vehicles.stream()
                .filter(v -> v.getId() == vehicleId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_NO_VEHICLE_ID, vehicleId)));
    }
}
