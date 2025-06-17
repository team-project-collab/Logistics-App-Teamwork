package com.company.oop.logistics.core.contracts;

import com.company.oop.logistics.models.contracts.Truck;

public interface VehicleService {


    Truck createVehicle(String truckName);

    Truck getVehicleById(int vehicleId);
}
