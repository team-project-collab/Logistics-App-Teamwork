package com.company.oop.logistics.models.contracts;

import com.company.oop.logistics.models.enums.TruckName;

public interface Truck extends Identifiable{
    TruckName getTruckName();
    public int getCapacity();
}
