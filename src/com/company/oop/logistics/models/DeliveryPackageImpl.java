package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.contracts.Location;

public class DeliveryPackageImpl implements DeliveryPackage {
    private  String id;
    private Location startLocation;
    private  Location endLocation;
    private double weightKg;
    private  String customerContactInfo;


    @Override
    public int getId() {
        return 0;
    }
}
