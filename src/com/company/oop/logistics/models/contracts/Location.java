package com.company.oop.logistics.models.contracts;

import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.models.enums.LocationType;

import java.time.LocalDateTime;

public interface Location extends Identifiable{
    City getName();

    LocalDateTime getArrivalTime();

    LocalDateTime getDepartureTime();

    void setDepartureTime(LocalDateTime departureTime);

    LocationType getType();
}