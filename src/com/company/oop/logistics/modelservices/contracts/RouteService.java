package com.company.oop.logistics.modelservices.contracts;

import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface RouteService {
    List<DeliveryRoute> getRoutes();

    List<DeliveryRoute> getRoutesInProgress();

    DeliveryRoute createDeliveryRoute(LocalDateTime startTime, List<City> cities);

    DeliveryRoute getRouteById(int routeId);

    void save();

    void assignVehicle(int vehicleId, int routeId);

    void assignPackage(int deliveryRouteId, int deliveryPackageId);

    ArrayList<DeliveryRoute> findRoutesServicingStartAndEnd(City origin, City destination);
}
