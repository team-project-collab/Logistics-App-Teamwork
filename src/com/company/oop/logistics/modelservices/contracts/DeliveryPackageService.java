package com.company.oop.logistics.modelservices.contracts;

import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryPackageService {
    DeliveryPackage createDeliveryPackage(City startLocation, City endLocation, double weightKg, CustomerContactInfo customerContactInfo);

    DeliveryPackage getDeliveryPackageById(int packageId);

    List<DeliveryPackage> getAllDeliveryPackages();

    List<DeliveryPackage> getUnassignedPackages();

    String getPackageState(int packageId, LocalDateTime time);

    void assignPackage(int deliveryRouteId, int packageId, List<Integer> locationIds);
}
