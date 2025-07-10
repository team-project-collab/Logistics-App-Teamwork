package com.company.oop.logistics.modelservices;

import com.company.oop.logistics.modelservices.contracts.DeliveryPackageService;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.DeliveryPackageImpl;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.misc.IdUtils;
import com.company.oop.logistics.utils.misc.LocationInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPackageServiceImpl implements DeliveryPackageService {
    private static final String storagePath = "data/deliveryPackages.xml";
    private static final String ERROR_NO_PACKAGE_ID = "No package with this id.";

    private final PersistenceManager persistenceManager;
    private final LocationService locationService;
    private final List<DeliveryPackage> packages;
    private int nextId;

    public DeliveryPackageServiceImpl(PersistenceManager persistenceManager, LocationService locationService) {
        this.persistenceManager = persistenceManager;
        this.locationService = locationService;
        packages = persistenceManager.loadData(storagePath);
        nextId = IdUtils.getNextId(packages);
    }

    private void save() {
        persistenceManager.saveData(packages, storagePath);
    }

    @Override
    public DeliveryPackage createDeliveryPackage(City startLocation, City endLocation,
                                                 double weightKg, CustomerContactInfo customerContactInfo) {
        DeliveryPackage p = new DeliveryPackageImpl(nextId, startLocation, endLocation,
                weightKg, customerContactInfo.getId());
        p.setLocations(List.of(locationService.createLocation(startLocation, LocalDateTime.now(), null).getId()));
        nextId++;
        this.packages.add(p);
        save();
        return p;
    }

    @Override
    public DeliveryPackage getDeliveryPackageById(int packageId) {
        return packages.stream()
                .filter(p -> p.getId() == packageId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ERROR_NO_PACKAGE_ID));
    }

    @Override
    public List<DeliveryPackage> getAllDeliveryPackages(){
        return new ArrayList<>(packages);
    }

    @Override
    public List<DeliveryPackage> getUnassignedPackages(){
        return packages.stream()
                .filter(p -> !p.isAssigned())
                .toList();
    }

    @Override
    public String getPackageState(int packageId, LocalDateTime time) {
        DeliveryPackage deliveryPackage = getDeliveryPackageById(packageId);
        LocationInfo locationInfo = new LocationInfo(locationService, deliveryPackage.getLocations(), time);
        return locationInfo.getPackageStatus();
    }

    @Override
    public void assignPackage(int deliveryRouteId, int packageId, List<Integer> locationIds) {
        DeliveryPackage deliveryPackage = getDeliveryPackageById(packageId);
        deliveryPackage.setLocations(locationIds);

        deliveryPackage.assign(deliveryRouteId);
        save();
    }
}


