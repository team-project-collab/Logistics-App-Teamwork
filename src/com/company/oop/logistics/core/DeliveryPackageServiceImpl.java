package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.DeliveryPackageService;
import com.company.oop.logistics.core.contracts.LocationService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.DeliveryPackageImpl;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.Identifiable;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.models.enums.PackageStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPackageServiceImpl implements DeliveryPackageService {

    private final String storagePath = "data/deliveryPackages.xml";
    private final PersistenceManager persistenceManager = new PersistenceManager();
    public static final String ERROR_NO_PACKAGE_ID = "No package with this id.";
    public static final String ERROR_PACKAGE_ALREADY_ASSIGNED = "Package is already assigned.";
    public static final String ERROR_GETTING_PACKAGE_STATUS = "Error getting package status";
    public static final String ERROR_BAD_CALL_STATIONARY = "Illegal method call for package that is not stationary";
    public static final String ERROR_BAD_CALL_IN_TRANSIT = "Illegal method call for package that is not in transit";
    public static final String MESSAGE_PACKAGE_STATUS_NOT_ASSIGNED = "Package is not yet assigned";
    public static final String MESSAGE_PACKAGE_STATUS_SCHEDULED = "Package is scheduled but has not yet entered transit";
    public static final String MESSAGE_PACKAGE_STATUS_STATIONARY = "Package is currently stationary in %s";
    public static final String MESSAGE_PACKAGE_STATUS_IN_TRANSIT = "Package is in transit. Currently traveling to: %s";
    public static final String MESSAGE_PACKAGE_STAUTS_DELIVERED = "Package is delivered to %s";

    private final LocationService locationService;
    private int nextId;
    List<DeliveryPackage> packages = new ArrayList<>();



    public DeliveryPackageServiceImpl(LocationService locationService) {
        this.locationService = locationService;
        load();
    }

    public void save() {
        persistenceManager.saveData(packages, storagePath);
    }

    public void load() {
        List<DeliveryPackage> loaded = persistenceManager.loadData(storagePath);
        if (loaded != null) {
            this.packages = loaded;
        }
        nextId = packages.stream().mapToInt(Identifiable::getId).max().orElse(0) + 1;
    }


    @Override
    public DeliveryPackage createDeliveryPackage(City startLocation, City endLocation,
                                                 double weightKg, CustomerContactInfo customerContactInfo) {
        DeliveryPackage p = new DeliveryPackageImpl(nextId, startLocation, endLocation,
                weightKg, customerContactInfo.getId());
        nextId++;
        this.packages.add(p);
        save();
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

    @Override
    public void assignPackage(int packageId, int deliveryRouteId) {
        DeliveryPackage deliveryPackage = getDeliveryPackageById(packageId);
        if (deliveryPackage.isAssigned()) {
            throw new IllegalStateException(ERROR_PACKAGE_ALREADY_ASSIGNED);
        }
        save();
    }

    @Override
    public String getPackageState(int packageId, LocalDateTime time) {
        DeliveryPackage deliveryPackage = getDeliveryPackageById(packageId);
        return getPackageStatusDescription(packageId, time);
    }

    public List<DeliveryPackage> getUnassignedPackages(LocalDateTime time){
        return packages.stream()
                .filter(p -> getPackageStatus(p.getId(), time).equals(PackageStatus.NOT_ASSIGNED))
                .toList();
    }

    public PackageStatus getPackageStatus(int packageId, LocalDateTime time) {
        DeliveryPackage currentPackage = getDeliveryPackageById(packageId);
        if (currentPackage.getLocations() == null || currentPackage.getLocations().isEmpty()) {
            return PackageStatus.NOT_ASSIGNED;
        }
        List<Location> locations = currentPackage.getLocations().stream()
                .map(locationService::getLocationById).toList();
        if (time.isBefore(locations.get(0).getDepartureTime())) {
            return PackageStatus.SCHEDULED;
        }
        for (Location location : locations) {
            if (time.isBefore(location.getDepartureTime())) {
                if (time.isAfter(location.getArrivalTime())) {
                    return PackageStatus.STATIONARY;
                } else {
                    return PackageStatus.IN_TRANSIT;
                }
            }
        }
        Location last = locations.get(locations.size() - 1);
        if (time.isAfter(last.getArrivalTime())) {
            return PackageStatus.DELIVERED;
        }
        throw new RuntimeException(ERROR_GETTING_PACKAGE_STATUS);
    }




    public String getPackageStatusDescription(int packageId, LocalDateTime time) {
        PackageStatus state = getPackageStatus(packageId, time);
        switch (state) {
            case NOT_ASSIGNED:
                return MESSAGE_PACKAGE_STATUS_NOT_ASSIGNED;
            case SCHEDULED:
                return MESSAGE_PACKAGE_STATUS_SCHEDULED;
            case STATIONARY:
                return String.format(MESSAGE_PACKAGE_STATUS_STATIONARY, getCurrentLocation(packageId, time).getName());
            case IN_TRANSIT:
                return String.format(MESSAGE_PACKAGE_STATUS_IN_TRANSIT, getNextLocation(packageId, time).getName());
            case DELIVERED:
                return String.format(MESSAGE_PACKAGE_STAUTS_DELIVERED, getLastLocation(packageId).getName());
            default:
                return null;
        }
    }

    private Location getCurrentLocation(int packageId, LocalDateTime time){
        DeliveryPackage currentPackage = getDeliveryPackageById(packageId);
        List<Location> locations = currentPackage.getLocations().stream()
                .map(locationService::getLocationById).toList();
        return locations.stream()
                .filter(l -> l.getArrivalTime().isBefore(time) && l.getDepartureTime().isAfter(time))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ERROR_BAD_CALL_STATIONARY));
    }

    private Location getNextLocation(int packageId, LocalDateTime time){
        DeliveryPackage currentPackage = getDeliveryPackageById(packageId);
        List<Location> locations = currentPackage.getLocations().stream()
                .map(locationService::getLocationById).toList();
        return locations.stream()
                .filter(l -> l.getDepartureTime().isAfter(time))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ERROR_BAD_CALL_IN_TRANSIT));
    }

    private Location getLastLocation(int packageId){
        DeliveryPackage currentPackage = getDeliveryPackageById(packageId);
        List<Location> locations = currentPackage.getLocations().stream()
                .map(locationService::getLocationById).toList();
        return locations.get(locations.size() - 1);
    }

}


