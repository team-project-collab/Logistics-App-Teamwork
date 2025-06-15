package com.company.oop.logistics.core.contracts;

import com.company.oop.logistics.models.contracts.Truck;

public interface VehicleService {
    boolean isVehicleAssigned(Truck vehicle);

    Truck createVehicle(String truckName);
}
