package com.company.oop.logistics.core.contracts;

import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface ObjectRepository {
    Location createLocation(City name, LocalDateTime arrivalTime, LocalDateTime departureTime);

    List<Location> getLocations();

    List<DeliveryRoute> getRoutes();

    DeliveryRoute createDeliveryRoute(LocalDateTime startTime, ArrayList<Location> locations);

    void assignVehicleToRoute(int vehicleId, int deliveryRouteId);

    boolean isVehicleAssigned(Truck vehicle);

    Truck createVehicle(String truckName);
    public void assignPackage(int packageId, int deliveryRouteId);
}
