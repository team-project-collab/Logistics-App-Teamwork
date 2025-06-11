package com.company.oop.logistics.models.contracts;

public interface DeliveryPackage extends Identifiable{
    public double getWeightKg();
    public boolean isAssigned();
}
