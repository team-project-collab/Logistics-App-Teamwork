package com.company.oop.logistics.models.contracts;

import com.company.oop.logistics.models.enums.TruckName;

import java.util.List;

public interface Truck extends Identifiable{
    TruckName getTruckName();

    int getCapacity();

    List<Integer> getLocationIds();

    void addLocationIds(List<Integer> locationIds);

    int getMaxRange();
}
