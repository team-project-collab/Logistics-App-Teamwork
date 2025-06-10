package com.company.oop.logistics.models.contracts;

import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;

public interface Location{
    City getName();

    LocalDateTime getArrivalTime();

    LocalDateTime getDepartureTime();

    void setDepartureTime(LocalDateTime departureTime);
}