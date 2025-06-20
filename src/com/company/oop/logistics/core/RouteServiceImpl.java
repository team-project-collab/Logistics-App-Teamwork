package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.LocationService;
import com.company.oop.logistics.core.contracts.RouteService;
import com.company.oop.logistics.core.contracts.VehicleService;
import com.company.oop.logistics.models.DeliveryRouteImpl;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.constants.CityDistance;
import com.company.oop.logistics.utils.misc.ComparingHelpers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class RouteServiceImpl implements RouteService {
    public static final String ERROR_VEHICLE_ALREADY_ASSIGNED = "Vehicle %d is already assigned to another route";
    public static final String ERROR_NO_ROUTE_ID = "There is no delivery route with id %s.";
    public static final String ERROR_ORIGIN_EQUALS_DESTINATION = "Origin and destination must be different.";
    public static final int INT_TRUCK_SPEED = 87;
    public static final int RESTING_MINUTES = 60;

    private final VehicleService vehicleService;
    private final LocationService locationService;
    private int nextId;
    List<DeliveryRoute> routes = new ArrayList<>();

    public RouteServiceImpl(VehicleService vehicleService, LocationService locationService) {
        nextId = 0;
        this.vehicleService = vehicleService;
        this.locationService = locationService;
    }

    @Override
    public List<DeliveryRoute> getRoutes() {
        return routes;
    }

    @Override
    public DeliveryRoute createDeliveryRoute(LocalDateTime startTime, ArrayList<City> cities) {
        DeliveryRoute route = new DeliveryRouteImpl(++nextId, startTime, generateRouteLocations(startTime, cities));
        this.routes.add(route);
        return route;
    }

    public ArrayList<Location> generateRouteLocations(LocalDateTime startTime, ArrayList<City> cities) {
        ArrayList<Location> result = new ArrayList<>();
        LocalDateTime currentTime = startTime;
        for (int i = 0; i < cities.size(); i++) {
            int timeToTravel = 0;
            if (i < cities.size() - 1) {
                timeToTravel = (int) ((float) CityDistance.getDistance(cities.get(i), cities.get(i + 1))
                        / INT_TRUCK_SPEED * 60) * 60;
            }
            result.add(locationService.createLocation(cities.get(i),
                    currentTime, currentTime.plusMinutes(RESTING_MINUTES)));
            currentTime = currentTime.plusMinutes(RESTING_MINUTES).plusSeconds(timeToTravel);
        }
        return result;
    }


    @Override
    public boolean isVehicleAssigned(Truck vehicle, LocalDateTime startTime, LocalDateTime endTime) {
        boolean result = false;
        for (DeliveryRoute route : routes) {
            Truck routeVehicle = route.getAssignedVehicle();
            if (routeVehicle != null && routeVehicle.getId() == vehicle.getId()) {
                if (ComparingHelpers.doTimeFramesOverlap(startTime, endTime,
                        route.getOrigin().getDepartureTime(),
                        route.getDestination().getArrivalTime())) {
                    result = true;
                }
            }
        }
        return result;
    }

    @Override
    public void assignVehicleToRoute(int vehicleId, int deliveryRouteId) {
        Truck vehicle = vehicleService.getVehicleById(vehicleId);
        DeliveryRoute route = getRouteById(deliveryRouteId);

        if (isVehicleAssigned(vehicle, route.getOrigin().getDepartureTime(), route.getDestination().getArrivalTime())) {
            throw new IllegalArgumentException(String.format(ERROR_VEHICLE_ALREADY_ASSIGNED, vehicle.getId()));
        }
        route.assignTruck(vehicle);
    }

    @Override
    public ArrayList<Integer> findRoutesServicingStartAndEnd(City origin, City destination) {
        if (origin.equals(destination)) {
            throw new IllegalArgumentException(ERROR_ORIGIN_EQUALS_DESTINATION);
        }
        ArrayList<Integer> result = new ArrayList<>();

        for (DeliveryRoute route : routes) {
            ArrayList<Location> routeLocations = route.getLocations();
            for (int i = 0; i < routeLocations.size() - 1; i++) {
                if (routeLocations.get(i).getName().equals(origin)) {
                    for (int j = i; j < routeLocations.size(); j++) {
                        if (routeLocations.get(j).getName().equals(destination)) {
                            result.add(route.getId());
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public DeliveryRoute getRouteById(int deliveryRouteId) {
        return routes.stream()
                .filter(r -> r.getId() == deliveryRouteId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_NO_ROUTE_ID, deliveryRouteId)));
    }
}
