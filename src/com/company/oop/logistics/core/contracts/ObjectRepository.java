package com.company.oop.logistics.core.contracts;

import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
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

    DeliveryRoute createDeliveryRoute(LocalDateTime startTime, ArrayList<City> cities);

    DeliveryPackage createDeliveryPackage(Location startLocation, Location endLocation, double weightKg, CustomerContactInfo customerContactInfo);

    void assignVehicleToRoute(int vehicleId, int deliveryRouteId);

    boolean isVehicleAssigned(Truck vehicle);

    Truck createVehicle(String truckName);

    public void assignPackage(int packageId, int deliveryRouteId);

    Location getLocationById(int startLocationId);

    CustomerContactInfo getCustomerContactById(int customerContactInfoId);

    ArrayList<Integer> findRoutesServicingStartAndEnd(City origin, City destination);
}
