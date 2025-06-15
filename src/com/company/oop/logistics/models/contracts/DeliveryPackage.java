package com.company.oop.logistics.models.contracts;

import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface DeliveryPackage extends Identifiable{
    public double getWeightKg();
    public boolean isAssigned();

    ArrayList<Location> getLocations();

    void setLocations(ArrayList<Location> locations);
    City getStartLocation();
    City getEndLocation();
    String getState(LocalDateTime time);
}
