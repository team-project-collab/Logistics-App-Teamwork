package com.company.oop.logistics.core.contracts;

import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface RouteService {
    List<DeliveryRoute> getRoutes();

    DeliveryRoute createDeliveryRoute(LocalDateTime startTime, ArrayList<City> cities);

    void assignVehicleToRoute(int vehicleId, int deliveryRouteId);

    ArrayList<Integer> findRoutesServicingStartAndEnd(City origin, City destination);

    DeliveryRoute getRouteById(int routeId);
}
