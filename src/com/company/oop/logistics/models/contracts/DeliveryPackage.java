package com.company.oop.logistics.models.contracts;

import com.company.oop.logistics.models.enums.City;
import java.util.ArrayList;

public interface DeliveryPackage extends Identifiable{
    double getWeightKg();
    boolean isAssigned();

    ArrayList<Integer> getLocations();

    void setLocations(ArrayList<Integer> locationIds);
    City getStartLocation();
    City getEndLocation();
}
