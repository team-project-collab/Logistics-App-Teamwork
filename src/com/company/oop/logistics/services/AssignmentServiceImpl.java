package com.company.oop.logistics.services;

import com.company.oop.logistics.exceptions.custom.LimitBreak;
import com.company.oop.logistics.models.contracts.*;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.DeliveryPackageServiceImpl;
import com.company.oop.logistics.modelservices.contracts.DeliveryPackageService;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.modelservices.contracts.RouteService;
import com.company.oop.logistics.modelservices.contracts.VehicleService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssignmentServiceImpl implements AssignmentService{
    private static final String ERROR_LOW_RANGE = "The truck (id %d) does not have sufficient range to cover this route. Required %d, truck has only %d.";
    private static final String ERROR_VEHICLE_ALREADY_ASSIGNED = "Vehicle %d is already assigned to another route at this time";
    private static final String ERROR_NO_VEHICLE = "Route %d has no vehicle yet.";
    private static final String ERROR_ROUTE_STARTED_BEFORE_PACKAGE_ASSIGN = "Cannot assign the package, as the route already left the starting location";
    private static final String ERROR_VEHICLE_LOCATION = "Vehicle is not stationed in the correct city.";
    private static final String ERROR_PACKAGE_ALREADY_ASSIGNED = "Package is already assigned.";
    private static final String ERROR_TRUCK_CAPACITY = "Exceeds capacity of truck";

    private final RouteService deliveryRouteService;
    private final LocationService locationService;
    private final VehicleService vehicleService;
    private final DeliveryPackageService deliveryPackageService;

    public AssignmentServiceImpl(RouteService deliveryRouteService,
                                 LocationService locationService,
                                 VehicleService vehicleService,
                                 DeliveryPackageService deliveryPackageService) {
        this.deliveryRouteService = deliveryRouteService;
        this.locationService = locationService;
        this.vehicleService = vehicleService;
        this.deliveryPackageService = deliveryPackageService;
    }


    @Override
    public void assignVehicleToRoute(int vehicleId, int deliveryRouteId) {
        Truck vehicle = vehicleService.getVehicleById(vehicleId);
        List<Location> vehicleLocations = vehicle.getLocationIds().stream()
                .map(locationService::getLocationById).toList();

        DeliveryRoute route = deliveryRouteService.getRouteById(deliveryRouteId);
        Location origin = locationService.getLocationById(route.getOrigin());

        validateVehicleRange(vehicle, route);
        validateVehicleFree(vehicleId, origin.getDepartureTime());
        validateVehicleLocation(vehicleLocations, origin.getName());

        deliveryRouteService.assignVehicle(vehicleId, deliveryRouteId);
        vehicleService.assignVehicle(vehicleId, route.getLocations());
    }

    @Override
    public void assignPackage(int deliveryRouteId, int deliveryPackageId) {
        DeliveryRoute deliveryRoute = deliveryRouteService.getRouteById(deliveryRouteId);
        DeliveryPackage deliveryPackage = deliveryPackageService.getDeliveryPackageById(deliveryPackageId);
        List<Location> locationsToAdd = deliveryRouteService.getMatchingLocations(
                deliveryRouteId,
                deliveryPackage.getStartLocation(),
                deliveryPackage.getEndLocation());

        validateVehicleIsAssigned(deliveryRoute);
        validateRouteCapacity(deliveryPackage, deliveryRoute);
        validateRouteNotStarted(locationsToAdd);
        validatePackageNotAssigned(deliveryPackage);

        List<Location> locationsToAddTrimmed = locationService.trimLocations(locationsToAdd);

        deliveryRoute.addPackage(deliveryPackage.getId());

        List<Integer> locationIds = locationsToAddTrimmed.stream().map(Identifiable::getId).toList();

        deliveryPackageService.assignPackage(deliveryRouteId, deliveryPackageId, locationIds);
        deliveryRouteService.assignPackage(deliveryRouteId, deliveryPackageId);
    }

    @Override
    public int bulkAssignPackages(int deliveryRouteId) {
        List<DeliveryPackage> unassignedPackages = deliveryPackageService.getUnassignedPackages();
        int assignedPackages = 0;
        Logger log = Logger.getLogger(DeliveryPackageServiceImpl.class.getName());
        for (DeliveryPackage deliveryPackage : unassignedPackages) {
            try {
                assignPackage(deliveryRouteId, deliveryPackage.getId());
                assignedPackages++;
            } catch (RuntimeException e) {
                log.log(Level.WARNING, "Failed to assign package with ID: " + deliveryPackage.getId(), e);
            }
        }
        return assignedPackages;
    }

    public HashMap<City, Double> getLoad(int routeId, City startLocation, City endLocation){
        boolean withinSubroute = false;
        HashMap <City, Double> result = new HashMap<>();
        DeliveryRoute route = deliveryRouteService.getRouteById(routeId);
        List<Location> locations = route.getLocations().stream()
                .map(locationService::getLocationById).toList();
        List<DeliveryPackage> assignedPackages = route.getAssignedPackages().stream()
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

    public double getMaxLoad(int routeId, City startLocation, City endLocation){
        return Collections.max(getLoad(routeId, startLocation, endLocation).entrySet(), Map.Entry.comparingByValue()).getValue();
    }

    public double getFreeCapacity(int routeId, City startLocation, City endLocation){
        DeliveryRoute route = deliveryRouteService.getRouteById(routeId);
        double capacity = vehicleService.getVehicleById(route.getAssignedVehicleId()).getCapacity();
        double load = getMaxLoad(routeId, startLocation, endLocation);
        return capacity - load;
    }

    private void validateVehicleRange(Truck vehicle, DeliveryRoute route){
        if (vehicle.getMaxRange() < route.getDistance()){
            throw new IllegalArgumentException(String.format(ERROR_LOW_RANGE,
                    vehicle.getId(), route.getDistance(), vehicle.getMaxRange()));
        }
    }

    private void validateVehicleFree(int vehicleId, LocalDateTime time){
        if (!vehicleService.isVehicleFree(vehicleId, time)) {
            throw new IllegalArgumentException(String.format(ERROR_VEHICLE_ALREADY_ASSIGNED, vehicleId));
        }
    }

    private void validateVehicleLocation(List<Location> vehicleLocations, City target){
        if (vehicleLocations.isEmpty()){
            throw new IllegalStateException(ERROR_VEHICLE_LOCATION);
        }
        if (!vehicleLocations.get(vehicleLocations.size() - 1).getName().equals(target)) {
            throw new IllegalStateException(ERROR_VEHICLE_LOCATION);
        }
    }

    private void validateVehicleIsAssigned(DeliveryRoute deliveryRoute){
        if (deliveryRoute.getAssignedVehicleId() == 0){
            throw new IllegalStateException(String.format(ERROR_NO_VEHICLE, deliveryRoute.getId()));
        }
    }

    private void validatePackageNotAssigned(DeliveryPackage deliveryPackage){
        if (deliveryPackage.isAssigned()) {
            throw new IllegalStateException(ERROR_PACKAGE_ALREADY_ASSIGNED);
        }
    }

    private void validateRouteCapacity(DeliveryPackage deliveryPackage, DeliveryRoute deliveryRoute){
        Truck assignedVehicle = vehicleService.getVehicleById(deliveryRoute.getAssignedVehicleId());
        double packageWeight = deliveryPackage.getWeightKg();
        double loadForSubRoute = getMaxLoad(
                deliveryRoute.getId(),
                deliveryPackage.getStartLocation(),
                deliveryPackage.getEndLocation());

        if(packageWeight + loadForSubRoute > assignedVehicle.getCapacity()){
            throw new LimitBreak(ERROR_TRUCK_CAPACITY);
        }
    }

    private void validateRouteNotStarted(List<Location> locations){
        if (locations.get(0).getDepartureTime().isBefore(LocalDateTime.now())){
            throw new IllegalStateException(ERROR_ROUTE_STARTED_BEFORE_PACKAGE_ASSIGN);
        }
    }
}
