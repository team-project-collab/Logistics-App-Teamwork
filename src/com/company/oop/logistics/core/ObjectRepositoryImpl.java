package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.ObjectRepository;
import com.company.oop.logistics.models.*;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ObjectRepositoryImpl implements ObjectRepository {
    public static final String ERROR_NO_VEHICLE_ID = "There is no vehicle with id %s.";
    public static final String ERROR_NO_ROUTE_ID = "There is no delivery route with id %s.";
    public static final String ERROR_VEHICLE_ALREADY_ASSIGNED = "Vehicle %d is already assigned to another route";
    public static final String ERROR_NO_CUSTOMER_ID = "No customer contact with this id.";
    public static final String ERROR_NO_PACKAGE_ID = "No package with this id.";
    public static final String ERROR_NO_LOCATION_ID = "No location with this id.";
    public static final String ERROR_PACKAGE_ALREADY_ASSIGNED = "Package is already assigned.";

    private static int nextId;

    //TODO: locations list is probably not needed, as they are stored in route
    List<Location> locations = new ArrayList<>();
    //TODO: Add lists for the objects we will store
    List<DeliveryRoute> routes = new ArrayList<>();

    private List<CustomerContactInfo> customerContacts = new ArrayList<>();
    List<Truck> vehicles = new ArrayList<>();
    List<DeliveryPackage> packages = new ArrayList<>();


    public ObjectRepositoryImpl() {
        nextId = 0;
    }

    public Location createLocation(City name, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        return new LocationImpl(name, arrivalTime, departureTime);
    }

    public Location getLocationById(int locationId) {
        for (Location location : locations) {
            if (location.getId() == locationId) {
                return location;
            }
        }
        throw new IllegalArgumentException(ERROR_NO_LOCATION_ID);
    }

    public DeliveryRoute createDeliveryRoute(LocalDateTime startTime, ArrayList<Location> locations) {
        DeliveryRoute route = new DeliveryRouteImpl(++nextId, startTime, locations);
        this.routes.add(route);
        return route;
    }


    public Truck createVehicle(String truckName) {
        Truck vehicle = new TruckImpl(truckName);
        this.vehicles.add(vehicle);
        return vehicle;
    }

    @Override
    public List<Location> getLocations() {
        return locations;
    }

    @Override
    public List<DeliveryRoute> getRoutes() {
        return routes;
    }

    public void assignVehicleToRoute(int vehicleId, int deliveryRouteId) {
        Truck vehicle = getVehicleById(vehicleId);
        DeliveryRoute route = getRouteById(deliveryRouteId);

        if (isVehicleAssigned(vehicle)) {
            throw new IllegalArgumentException(String.format(ERROR_VEHICLE_ALREADY_ASSIGNED, vehicle.getId()));
        }
        route.assignTruck(vehicle);
    }

    public boolean isVehicleAssigned(Truck vehicle) {
        for (DeliveryRoute route : routes) {
            if (vehicle.equals(route.getAssignedVehicle())) {
                return true;
            }
        }
        return false;
    }

    public Truck getVehicleById(int vehicleId) {
        for (Truck vehicle : vehicles) {
            if (vehicle.getId() == vehicleId) {
                return vehicle;
            }
        }
        throw new IllegalArgumentException(String.format(ERROR_NO_VEHICLE_ID, vehicleId));
    }

    public DeliveryRoute getRouteById(int deliveryRouteId) {
        for (DeliveryRoute route : routes) {
            if (route.getId() == deliveryRouteId) {
                return route;
            }
        }
        throw new IllegalArgumentException(String.format(ERROR_NO_ROUTE_ID, deliveryRouteId));
    }

    public void assignPackage(int packageId, int deliveryRouteId) {
        DeliveryPackage deliveryPackage = getDeliveryPackageById(packageId);
        DeliveryRoute route = getRouteById(deliveryRouteId);
        if (deliveryPackage.isAssigned()) {
            throw new IllegalStateException(ERROR_PACKAGE_ALREADY_ASSIGNED);
        }
        route.assignPackage(deliveryPackage);
    }

    public DeliveryPackage createDeliveryPackage(Location startLocation, Location endLocation, double weightKg, CustomerContactInfo customerContactInfo) {
        DeliveryPackage p = new DeliveryPackageImpl(++nextId, startLocation, endLocation, weightKg, customerContactInfo);
        this.packages.add(p);
        return p;

    }

    public DeliveryPackage getDeliveryPackageById(int packageId) {
        for (DeliveryPackage p :
                this.packages) {
            if (p.getId() == packageId) {
                return p;
            }
        }
        throw new IllegalArgumentException(ERROR_NO_PACKAGE_ID);
    }

    public CustomerContactInfo getCustomerContactById(int customerContactInfoId) {
        for (CustomerContactInfo contact : customerContacts) {
            if (contact.getId() == customerContactInfoId) {
                return contact;
            }
        }
        throw new IllegalArgumentException(ERROR_NO_CUSTOMER_ID);
    }

    public ArrayList<Integer> findRoutesServicingStartAndEnd(City origin, City destination){
        ArrayList<Integer> result = new ArrayList<>();
        for (DeliveryRoute route: routes){
            ArrayList<Location> routeLocations = route.getLocations();
            for (int i = 0; i < routeLocations.size() - 1; i++) {
                if (routeLocations.get(i).getName().equals(origin)){
                    for (int j = i; j < routeLocations.size(); j++) {
                        if (routeLocations.get(j).getName().equals(destination)){
                            result.add(route.getId());
                        }
                    }
                }
            }
        }
        return result;
    }
}
