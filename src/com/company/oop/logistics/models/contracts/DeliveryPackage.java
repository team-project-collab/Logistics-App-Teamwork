package com.company.oop.logistics.models.contracts;

import com.company.oop.logistics.models.enums.City;
import java.util.List;

public interface DeliveryPackage extends Identifiable{
    double getWeightKg();
  
    boolean isAssigned();

    List<Integer> getLocations();

    void setLocations(List<Integer> locationIds);
  
    City getStartLocation();
  
    City getEndLocation();

    int getCustomerContactInfoId();

    int getAssignedRoute();

    void assign(int routeId);

    void setAssigned(boolean assigned);
}
