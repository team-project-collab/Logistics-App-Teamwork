package com.company.oop.logistics.core.contracts;

import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;

import java.util.List;

public interface VehicleService {
    Truck createVehicle(String truckName, City initLocation);

    Truck getVehicleById(int vehicleId);

    List<Truck> getVehicles();

    void assignVehicle(int vehicleId, List<Integer> locationIds);
}
