package com.company.oop.logistics.modelservices.contracts;

import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleService {
    Truck createVehicle(String truckName, City initLocation);

    Truck getVehicleById(int vehicleId);

    List<Truck> getVehicles();

    void assignVehicle(int vehicleId, List<Integer> locationIds);

    boolean isVehicleFree(int vehicleId, LocalDateTime time);

    Location getCurrentLocation(int vehicleId, LocalDateTime time);
}
