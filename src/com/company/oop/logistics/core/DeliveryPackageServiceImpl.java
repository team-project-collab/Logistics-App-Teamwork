package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.DeliveryPackageService;
import com.company.oop.logistics.core.contracts.LocationService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.DeliveryPackageImpl;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.Identifiable;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.misc.LocationInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPackageServiceImpl implements DeliveryPackageService {

    private final String storagePath = "data/deliveryPackages.xml";
    private final PersistenceManager persistenceManager;
    public static final String ERROR_NO_PACKAGE_ID = "No package with this id.";
    public static final String ERROR_PACKAGE_ALREADY_ASSIGNED = "Package is already assigned.";

    private final LocationService locationService;
    private int nextId;
    private final List<DeliveryPackage> packages;



    public DeliveryPackageServiceImpl(PersistenceManager persistenceManager, LocationService locationService) {
        this.persistenceManager = persistenceManager;
        this.locationService = locationService;
        packages = persistenceManager.loadData(storagePath);
        nextId = packages.stream().mapToInt(Identifiable::getId).max().orElse(0) + 1;
    }

    public void save() {
        persistenceManager.saveData(packages, storagePath);
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
    public void assignPackage(int deliveryRouteId, int packageId) {
        DeliveryPackage deliveryPackage = getDeliveryPackageById(packageId);
        if (deliveryPackage.isAssigned()) {
            throw new IllegalStateException(ERROR_PACKAGE_ALREADY_ASSIGNED);
        }
        deliveryPackage.assign(deliveryRouteId);
        save();
    }

    @Override
    public String getPackageState(int packageId, LocalDateTime time) {
        DeliveryPackage deliveryPackage = getDeliveryPackageById(packageId);
        LocationInfo locationInfo = new LocationInfo(locationService, deliveryPackage.getLocations(), time);
        return locationInfo.getPackageStatus();
    }

    public List<DeliveryPackage> getUnassignedPackages(LocalDateTime time){
        return packages.stream()
                .filter(p -> p.getLocations() == null || p.getLocations().isEmpty())
                .toList();
    }
}


