package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.DeliveryPackageService;
import com.company.oop.logistics.core.contracts.LocationService;
import com.company.oop.logistics.core.contracts.RouteService;
import com.company.oop.logistics.core.contracts.VehicleService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.exceptions.custom.LimitBreak;
import com.company.oop.logistics.models.DeliveryRouteImpl;
import com.company.oop.logistics.models.contracts.*;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.constants.CityDistance;
import com.company.oop.logistics.utils.misc.ComparingHelpers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class RouteServiceImpl implements RouteService {
    private final String storagePath = "data/routes.xml";
    private final PersistenceManager persistenceManager;
    public static final String ERROR_VEHICLE_ALREADY_ASSIGNED = "Vehicle %d is already assigned to another route at this time";
    public static final String ERROR_NO_ROUTE_ID = "There is no delivery route with id %s.";
    public static final String ERROR_ORIGIN_EQUALS_DESTINATION = "Origin and destination must be different.";
    public static final String ERROR_NO_VEHICLE = "Route %d has no vehicle yet.";
    public static final String ERROR_CITIES_NOT_UNIQUE = "One route can visit the same city only once.";
    public static final String ERROR_ROUTE_STARTED_BEFORE_PACKAGE_ASSIGN = "Cannot assign the package, as the route already left the starting location";
    public static final int INT_TRUCK_SPEED = 87;
    public static final int RESTING_MINUTES = 60;

    private final VehicleService vehicleService;
    private final LocationService locationService;
    private final DeliveryPackageService deliveryPackageService;
    private int nextId;
    private final List<DeliveryRoute> routes;

    public RouteServiceImpl(PersistenceManager persistenceManager, VehicleService vehicleService, LocationService locationService, DeliveryPackageService deliveryPackageService) {
        this.persistenceManager = persistenceManager;
        this.vehicleService = vehicleService;
        this.locationService = locationService;
        this.deliveryPackageService = deliveryPackageService;
        routes = persistenceManager.loadData(storagePath);
        nextId = routes.stream().mapToInt(Identifiable::getId).max().orElse(0) + 1;
    }

    public void save() {
        persistenceManager.saveData(routes, storagePath);
    }

    @Override
    public List<DeliveryRoute> getRoutes() {
        return routes;
    }

    @Override
    public DeliveryRoute createDeliveryRoute(LocalDateTime startTime, ArrayList<City> cities) {
        List<Integer> locations = generateRouteLocations(startTime, cities);
        DeliveryRoute route = new DeliveryRouteImpl(nextId, startTime, locations);
        nextId++;
        this.routes.add(route);
        save();
        return route;
    }

    public List<Integer> generateRouteLocations(LocalDateTime startTime, ArrayList<City> cities) {
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


    @Override
    public boolean isVehicleAssigned(Truck vehicle, LocalDateTime startTime) {
        boolean result = false;
        if (!vehicle.getLocationIds().isEmpty()) {
            LocalDateTime freeAfter = locationService
                    .getLocationById(vehicle.getLocationIds().getLast()).getArrivalTime();
            if (freeAfter.isAfter(startTime)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public void assignVehicleToRoute(int vehicleId, int deliveryRouteId) {
        Truck vehicle = vehicleService.getVehicleById(vehicleId);
        List<Location> vehicleLocations = vehicle.getLocationIds().stream()
                .map(locationService::getLocationById).toList();

        DeliveryRoute route = getRouteById(deliveryRouteId);
        Location origin = locationService.getLocationById(route.getOrigin());

        if (isVehicleAssigned(vehicle,origin.getDepartureTime())) {
            throw new IllegalArgumentException(String.format(ERROR_VEHICLE_ALREADY_ASSIGNED, vehicle.getId()));
        }
        if (!vehicleLocations.isEmpty()){
            if (!vehicleLocations.getLast().getName().equals(origin.getName())) {
                throw new IllegalStateException("Vehicle is not stationed in the correct city at that time.");
            }
        }
        route.assignTruck(vehicle.getId());
        vehicleService.assignVehicle(vehicleId, route.getLocations());
        save();
    }

    @Override
    public ArrayList<Integer> findRoutesServicingStartAndEnd(City origin, City destination) {
        if (origin.equals(destination)) {
            throw new IllegalArgumentException(ERROR_ORIGIN_EQUALS_DESTINATION);
        }
        ArrayList<Integer> result = new ArrayList<>();

        for (DeliveryRoute route : routes) {
            List<Location> routeLocations = route.getLocations().stream()
                    .map(locationService::getLocationById).toList();
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
    public void assignPackage(int deliveryRouteId, int deliveryPackageId) {
        DeliveryRoute deliveryRoute = getRouteById(deliveryRouteId);
        DeliveryPackage deliveryPackage = deliveryPackageService.getDeliveryPackageById(deliveryPackageId);
        if (deliveryRoute.getAssignedVehicleId() == 0){
            throw new IllegalStateException(String.format(ERROR_NO_VEHICLE, deliveryRouteId));
        }

        Truck assignedVehicle = vehicleService.getVehicleById(deliveryRoute.getAssignedVehicleId());

        ArrayList<Location> locationsToAdd =
                getLocations(deliveryRouteId, deliveryPackage.getStartLocation(), deliveryPackage.getEndLocation());

        if((deliveryPackage.getWeightKg() +
                getMaxLoad(deliveryRouteId, deliveryPackage.getStartLocation(), deliveryPackage.getEndLocation()))
                > assignedVehicle.getCapacity()){
            throw new LimitBreak("Exceeds capacity of truck");
        }
        if (locationsToAdd.get(0).getDepartureTime().isBefore(LocalDateTime.now())){
            throw new IllegalStateException(ERROR_ROUTE_STARTED_BEFORE_PACKAGE_ASSIGN);
        }
        deliveryPackage.setLocations(locationsToAdd.stream().map(Identifiable::getId)
                .collect(Collectors.toCollection(ArrayList::new)));
        deliveryRoute.addPackage(deliveryPackage.getId());

        deliveryPackageService.assignPackage(deliveryRouteId, deliveryPackageId);
        save();
    }

    @Override
    public void bulkAssignPackages(int deliveryRouteId, LocalDateTime time) {
        List<DeliveryPackage> unassignedPackages = deliveryPackageService.getUnassignedPackages(time);
        Logger log = Logger.getLogger(DeliveryPackageServiceImpl.class.getName());
        for (DeliveryPackage deliveryPackage : unassignedPackages) {
            try {
                assignPackage(deliveryRouteId, deliveryPackage.getId());
            } catch (RuntimeException e) {
                log.log(Level.WARNING, "Failed to assign package with ID: " + deliveryPackage.getId(), e);
            }
        }
    }


    public HashMap<City, Double> getLoad(int routeId, City startLocation, City endLocation){
        boolean withinSubroute = false;
        HashMap <City, Double> result = new HashMap<>();
        List<Location> locations = getRouteById(routeId).getLocations().stream()
                .map(locationService::getLocationById).toList();
        List<DeliveryPackage> assignedPackages = getRouteById(routeId).getAssignedPackages().stream()
                .map(deliveryPackageService::getDeliveryPackageById).toList();
        for (Location location : locations) {
            if (location.getName().equals(startLocation)) {
                withinSubroute = true;
            }
            if (location.getName().equals(endLocation)) {
                withinSubroute = false;
            }
            if (withinSubroute) {
                double weightSum = 0;
                for (DeliveryPackage assignedPackage : assignedPackages) {
                    if (assignedPackage.getLocations() == null || assignedPackage.getLocations().isEmpty()){
                        continue;
                    }
                    List<Location> packageLocations = assignedPackage.getLocations()
                            .stream().map(locationService::getLocationById).toList();
                    packageLocations = new ArrayList<>(packageLocations.subList(0, packageLocations.size() - 1));
                    if (packageLocations.contains(location)) {
                        weightSum += assignedPackage.getWeightKg();
                    }
                }
                result.put(location.getName(), weightSum);
            }
        }
        return result;
    }

    private ArrayList<Location> getLocations(int routeId, City startLocation, City endLocation){
        boolean withinStartEnd = false;
        ArrayList<Location> packageLocations = new ArrayList<>();
        List<Location> locations = getRouteById(routeId).getLocations().stream()
                .map(locationService::getLocationById).toList();
        for (Location location : locations) {
            if (location.getName() == startLocation) {
                withinStartEnd = true;
            }
            if (withinStartEnd) {
                packageLocations.add(location);
            }
            if (withinStartEnd && location.getName() == endLocation) {
                return packageLocations;
            }
        }
        throw new IllegalArgumentException("Route does not service this package");
    }

    public double getMaxLoad(int routeId, City startLocation, City endLocation){
        return Collections.max(getLoad(routeId, startLocation, endLocation).entrySet(), Map.Entry.comparingByValue()).getValue();
    }



    @Override
    public DeliveryRoute getRouteById(int deliveryRouteId) {
        return routes.stream()
                .filter(r -> r.getId() == deliveryRouteId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_NO_ROUTE_ID, deliveryRouteId)));
    }
}
