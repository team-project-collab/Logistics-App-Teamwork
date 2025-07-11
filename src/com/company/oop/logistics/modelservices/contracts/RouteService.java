package com.company.oop.logistics.modelservices.contracts;

import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.List;

public interface RouteService {
    DeliveryRoute createDeliveryRoute(LocalDateTime startTime, List<City> cities);

    DeliveryRoute getRouteById(int routeId);

    List<DeliveryRoute> getAllRoutes();

    List<DeliveryRoute> getRoutesInProgress();

    List<DeliveryRoute> findRoutesServicingStartAndEnd(City origin, City destination);

    void assignVehicle(int vehicleId, int routeId);

    void assignPackage(int deliveryRouteId, int deliveryPackageId);

    List<Location> getMatchingLocations(int routeId, City startLocation, City endLocation);
}
