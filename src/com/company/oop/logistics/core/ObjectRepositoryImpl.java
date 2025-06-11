package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.ObjectRepository;
import com.company.oop.logistics.models.DeliveryRouteImpl;
import com.company.oop.logistics.models.LocationImpl;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ObjectRepositoryImpl implements ObjectRepository {
    public static final String ERROR_NO_VEHICLE_ID = "There is no vehicle with id %s.";
    public static final String ERROR_NO_ROUTE_ID = "There is no delivery route with id %s.";
    public static final String ERROR_VEHICLE_ALREADY_ASSIGNED = "Vehicle %d is already assigned to another route";
    private int nextId;

    //TODO: locations list is probably not needed, as they are stored in route
    List<Location> locations = new ArrayList<>();
    //TODO: Add lists for the objects we will store
    List<DeliveryRoute> routes = new ArrayList<>();

    List<Truck> vehicles = new ArrayList<>();


    public ObjectRepositoryImpl() {
        nextId = 0;
    }

    public Location createLocation(City name, LocalDateTime arrivalTime, LocalDateTime departureTime){
        return new LocationImpl(name, arrivalTime, departureTime);
    }

    public DeliveryRoute createDeliveryRoute(LocalDateTime startTime, ArrayList<Location> locations){
        DeliveryRoute route = new DeliveryRouteImpl(++nextId, startTime, locations);
        this.routes.add(route);
        return route;
    }

    public Truck createVehicle(String truckName){
        Truck vehicle = new TruckImpl(truckName);
        this.vehicles.add(vehicle);
        return vehicle;
    }

    @Override
    public List<Location> getLocations() {
        return locations;
    }

    @Override
    public List<DeliveryRoute> getRoutes(){
        return routes;
    }

    public void assignVehicleToRoute(int vehicleId, int deliveryRouteId){
        Truck vehicle = getVehicleById(vehicleId);
        DeliveryRoute route = getRouteById(deliveryRouteId);

        if (isVehicleAssigned(vehicle)){
            throw new IllegalArgumentException(String.format(ERROR_VEHICLE_ALREADY_ASSIGNED, vehicle.getId()));
        }
        route.assignTruck(vehicle);
    }

    public boolean isVehicleAssigned(Truck vehicle){
        for(DeliveryRoute route: routes){
            if (vehicle.equals(route.getAssignedVehicle())){
                return true;
            }
        }
        return false;
    }

    public Truck getVehicleById(int vehicleId){
        for (Truck vehicle: vehicles){
            if (vehicle.getId() == vehicleId){
                return vehicle;
            }
        }
        throw new IllegalArgumentException(String.format(ERROR_NO_VEHICLE_ID, vehicleId));
    }

    public DeliveryRoute getRouteById(int deliveryRouteId){
        for (DeliveryRoute route: routes){
            if (route.getId() == deliveryRouteId){
                return route;
            }
        }
        throw new IllegalArgumentException(String.format(ERROR_NO_ROUTE_ID, deliveryRouteId));
    }
}
