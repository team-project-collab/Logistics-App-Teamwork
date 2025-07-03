package com.company.oop.logistics.models.contracts;

import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.models.enums.PackageStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface DeliveryPackage extends Identifiable{
    double getWeightKg();
    boolean isAssigned();

    ArrayList<Location> getLocations();

    void setLocations(ArrayList<Location> locations);
    City getStartLocation();
    City getEndLocation();
    PackageStatus getPackageStatus(LocalDateTime time);
    String getPackageStatusDescription(LocalDateTime time);

    CustomerContactInfo getCustomerContactInfo();
}
