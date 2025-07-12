package com.company.oop.logistics.modelservices;

import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.modelservices.contracts.RouteService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.DeliveryRouteImpl;
import com.company.oop.logistics.models.contracts.*;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.constants.CityDistance;
import com.company.oop.logistics.utils.misc.IdUtils;
import com.company.oop.logistics.utils.validation.ValidationHelpers;

import java.time.LocalDateTime;
import java.util.*;


public class RouteServiceImpl implements RouteService {
    private static final String storagePath = "data/routes.xml";
    private static final String ERROR_NO_ROUTE_ID = "There is no delivery route with id %s.";
    private static final String ERROR_CITIES_NOT_UNIQUE = "One route can visit the same city only once.";
    private static final String ERROR_ORIGIN_EQUALS_DESTINATION = "Origin and destination must be different.";
    private static final String ERROR_ROUTE_LOCATIONS = "Route does not service this package";


    private static final int INT_TRUCK_SPEED = 87;
    private static final int RESTING_MINUTES = 60;

    private final PersistenceManager persistenceManager;
    private final LocationService locationService;
    private final List<DeliveryRoute> routes;
    private int nextId;

    public RouteServiceImpl(PersistenceManager persistenceManager, LocationService locationService) {
        this.persistenceManager = persistenceManager;
        this.locationService = locationService;
        routes = persistenceManager.loadData(storagePath);
        nextId = IdUtils.getNextId(routes);
    }

    private void save() {
        persistenceManager.saveData(routes, storagePath);
    }

    @Override
    public DeliveryRoute createDeliveryRoute(LocalDateTime startTime, List<City> cities) {
        ValidationHelpers.validateUniqueList(cities, ERROR_CITIES_NOT_UNIQUE);
        List<Integer> locations = generateRouteLocations(startTime, cities);
        int distance = CityDistance.getDistance(cities);
        DeliveryRoute route = new DeliveryRouteImpl(nextId, startTime, locations, distance);
        nextId++;
        this.routes.add(route);
        save();
        return route;
    }

    @Override
    public DeliveryRoute getRouteById(int deliveryRouteId) {
        return routes.stream()
                .filter(r -> r.getId() == deliveryRouteId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_NO_ROUTE_ID, deliveryRouteId)));
    }

    @Override
    public List<DeliveryRoute> getAllRoutes() {
        return new ArrayList<>(routes);
    }

    @Override
    public List<DeliveryRoute> getRoutesInProgress() {
        LocalDateTime now = LocalDateTime.now();
        List<DeliveryRoute> result = new ArrayList<>();
        for (DeliveryRoute route: routes){
            List<Location> routeLocations = route.getLocations().stream().map(locationService::getLocationById).toList();
            Location startLocation = routeLocations.get(0);
            Location endLocation = routeLocations.get(routeLocations.size() - 1);
            if (startLocation.getDepartureTime().isBefore(now) && endLocation.getArrivalTime().isAfter(now)){
                result.add(route);
            }
        }
        return result;
    }

    @Override
    public List<DeliveryRoute> findRoutesServicingStartAndEnd(City origin, City destination) {
        LocalDateTime now = LocalDateTime.now();
        if (origin.equals(destination)) {
            throw new IllegalArgumentException(ERROR_ORIGIN_EQUALS_DESTINATION);
        }
        ArrayList<DeliveryRoute> result = new ArrayList<>();

        for (DeliveryRoute route : routes) {
            List<Location> routeLocations = route.getLocations().stream()
                    .map(locationService::getLocationById).toList();
            for (int i = 0; i < routeLocations.size() - 1; i++) {
                if (routeLocations.get(i).getName().equals(origin) &&
                        routeLocations.get(i).getDepartureTime().isAfter(now)) {
                    for (int j = i; j < routeLocations.size(); j++) {
                        if (routeLocations.get(j).getName().equals(destination)) {
                            result.add(route);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void assignVehicle(int vehicleId, int routeId){
        DeliveryRoute route = getRouteById(routeId);
        route.assignTruck(vehicleId);
        save();
    }

    @Override
    public void assignPackage(int deliveryRouteId, int deliveryPackageId){
        DeliveryRoute deliveryRoute = getRouteById(deliveryRouteId);
        deliveryRoute.addPackage(deliveryPackageId);
        save();
    }

    private List<Integer> generateRouteLocations(LocalDateTime startTime, List<City> cities) {
        ArrayList<Location> result = new ArrayList<>();
        LocalDateTime arrivalTime = null;
        LocalDateTime departureTime = startTime;
        for (int i = 0; i < cities.size(); i++) {
            long timeToTravel = 0;
            if (i < cities.size() - 1) {
                timeToTravel = CityDistance.getTravelTimeSeconds(cities.get(i),cities.get(i + 1), INT_TRUCK_SPEED);
            }else{
                departureTime = null;
            }
            result.add(locationService.createLocation(cities.get(i), arrivalTime, departureTime));
            if (departureTime != null) {
                arrivalTime = departureTime.plusSeconds(timeToTravel);
                departureTime = arrivalTime.plusMinutes(RESTING_MINUTES);
            }
        }
        return result.stream().map(Location::getId).toList();
    }

    public List<Location> getMatchingLocations(int routeId, City startLocation, City endLocation){
        boolean withinStartEnd = false;
        List<Location> packageLocations = new ArrayList<>();
        List<Location> locations = getRouteById(routeId).getLocations().stream()
                .map(locationService::getLocationById).toList();
        for (Location location : locations) {
            if (location.getName().equals(startLocation)) {
                withinStartEnd = true;
            }
            if (withinStartEnd) {
                packageLocations.add(location);
            }
            if (withinStartEnd && location.getName().equals(endLocation)) {
                return packageLocations;
            }
        }
        throw new IllegalArgumentException(ERROR_ROUTE_LOCATIONS);
    }
}
