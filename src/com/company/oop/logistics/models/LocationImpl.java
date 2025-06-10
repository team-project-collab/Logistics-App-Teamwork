package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;

public class LocationImpl implements Location{
    public static final String ERROR_TIME_IN_THE_PAST = "%s cannot be in the past.";
    public static final String ATTRIBUTE_NAME_ARRIVAL_TIME = "arrival time";
    public static final String ATTRIBUTE_NAME_DEPARTURE_TIME = "departure time";
    public static final String ERROR_DEPARTURE_TIME_BEFORE_ARRIVAL_TIME = "Departure time cannot be before arrival time";

    private City name;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;

    public LocationImpl(City name, LocalDateTime arrivalTime, LocalDateTime departureTime){
        setName(name);
        setArrivalTime(arrivalTime);
    }

    public LocationImpl(City name, LocalDateTime arrivalTime){
        setName(name);
        setArrivalTime(arrivalTime);
    }

    private void setName(City name) {
        this.name = name;
    }

    private void setArrivalTime(LocalDateTime arrivalTime) {
        if (arrivalTime.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException(String.format(ERROR_TIME_IN_THE_PAST, ATTRIBUTE_NAME_ARRIVAL_TIME));
        }
        this.arrivalTime = arrivalTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        if (departureTime.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException(String.format(ERROR_TIME_IN_THE_PAST, ATTRIBUTE_NAME_DEPARTURE_TIME));
        }
        if (departureTime.isBefore(arrivalTime)){
            throw new IllegalArgumentException(ERROR_DEPARTURE_TIME_BEFORE_ARRIVAL_TIME);
        }
        this.departureTime = departureTime;
    }

    @Override
    public City getName() {
        return name;
    }

    @Override
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
}
