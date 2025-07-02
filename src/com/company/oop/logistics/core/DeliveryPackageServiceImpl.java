package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.DeliveryPackageService;
import com.company.oop.logistics.core.contracts.RouteService;
import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.DeliveryPackageImpl;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Identifiable;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.models.enums.PackageStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DeliveryPackageServiceImpl implements DeliveryPackageService {

    private final String storagePath = "data/deliveryPackages.xml";
    private final PersistenceManager persistenceManager = new PersistenceManager();
    public static final String ERROR_NO_PACKAGE_ID = "No package with this id.";
    public static final String ERROR_PACKAGE_ALREADY_ASSIGNED = "Package is already assigned.";

    private final RouteService routeService;
    private int nextId;
    List<DeliveryPackage> packages = new ArrayList<>();



    public DeliveryPackageServiceImpl(RouteService routeService) {
        this.routeService = routeService;
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
    public DeliveryPackage createDeliveryPackage(City startLocation, City endLocation, double weightKg, CustomerContactInfo customerContactInfo) {
        DeliveryPackage p = new DeliveryPackageImpl(nextId, startLocation, endLocation, weightKg, customerContactInfo);
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
        DeliveryRoute route = routeService.getRouteById(deliveryRouteId);
        if (deliveryPackage.isAssigned()) {
            throw new IllegalStateException(ERROR_PACKAGE_ALREADY_ASSIGNED);
        }
        route.assignPackage(deliveryPackage);
        save();
        routeService.save();
    }

    @Override
    public String getPackageState(int packageId, LocalDateTime time) {
        DeliveryPackage deliveryPackage = getDeliveryPackageById(packageId);
        return deliveryPackage.getPackageStatusDescription(time);
    }

    public ArrayList<DeliveryPackage> getUnassignedPackages(LocalDateTime time){
        return packages.stream()
                .filter(p -> p.getPackageStatus(time).equals(PackageStatus.NOT_ASSIGNED))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void bulkAssignPackages(int deliveryRouteId, LocalDateTime time) {
        ArrayList<DeliveryPackage> unassignedPackages = getUnassignedPackages(time);
        Logger log = Logger.getLogger(DeliveryPackageServiceImpl.class.getName());
        for (DeliveryPackage deliveryPackage : unassignedPackages) {
            try {
                assignPackage(deliveryPackage.getId(), deliveryRouteId);
            } catch (Exception e) {
                log.log(Level.WARNING, "Failed to assign package with ID: " + deliveryPackage.getId(), e);
            }
        }
    }


}
