package com.company.oop.logistics.core.contracts;

import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.List;

public interface RouteService {
    List<DeliveryRoute> getRoutes();

    DeliveryRoute createDeliveryRoute(LocalDateTime startTime, List<City> cities);

    void assignVehicleToRoute(int vehicleId, int deliveryRouteId);

    boolean isVehicleAssigned(Truck vehicle, LocalDateTime startTime);

    DeliveryRoute getRouteById(int routeId);

    void save();

    void assignPackage(int deliveryRouteId, int packageId);

    double getMaxLoad(int routeId, City startLocation, City endLocation);

    void bulkAssignPackages(int deliveryRouteId, LocalDateTime time);

    double getFreeCapacity(int routeId, City startLocation, City endLocation);
}
