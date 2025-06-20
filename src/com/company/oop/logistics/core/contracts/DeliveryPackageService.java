package com.company.oop.logistics.core.contracts;

import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface DeliveryPackageService {
    DeliveryPackage createDeliveryPackage(City startLocation, City endLocation, double weightKg, CustomerContactInfo customerContactInfo);

    void assignPackage(int packageId, int deliveryRouteId);

    String getPackageState(int packageId, LocalDateTime time);

    DeliveryPackage getDeliveryPackageById(int packageId);

    ArrayList<DeliveryPackage> getUnassignedPackages(LocalDateTime time);
}
