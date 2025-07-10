package com.company.oop.logistics.services;

import com.company.oop.logistics.models.enums.City;


public interface AssignmentService {

    void assignVehicleToRoute(int vehicleId, int deliveryRouteId);

    void assignPackage(int deliveryRouteId, int packageId);

    int bulkAssignPackages(int deliveryRouteId);

    double getMaxLoad(int routeId, City startLocation, City endLocation);

    double getFreeCapacity(int routeId, City startLocation, City endLocation);
}
