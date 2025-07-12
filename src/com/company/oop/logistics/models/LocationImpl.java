package com.company.oop.logistics.models;

import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.models.enums.LocationType;
import com.company.oop.logistics.utils.validation.ValidationHelpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocationImpl implements Location {
    private static final String ATTRIBUTE_NAME_ARRIVAL_TIME = "arrival time";
    private static final String ATTRIBUTE_NAME_DEPARTURE_TIME = "departure time";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final String LOCATION_TO_STRING_BASE = """
             * City: %s
            """;
    private static final String LOCATION_TO_STRING_ARRIVING = """
              - Arriving at: %s
            """;
    private static final String LOCATION_TO_STRING_DEPARTING = """
              - Departing at: %s
            """;

    private int id;
    private City name;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private LocationType type;

    public LocationImpl(int id, City name, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        setId(id);
        setName(name);
        setArrivalTime(arrivalTime);
        setDepartureTime(departureTime);
        setType();
    }

    private void setId(int id){
        this.id = id;
    }

    private void setName(City name) {
        this.name = name;
    }

    private void setArrivalTime(LocalDateTime arrivalTime) {
        if (arrivalTime != null) {
            ValidationHelpers.validateTimeAgainstPresent(arrivalTime, ATTRIBUTE_NAME_ARRIVAL_TIME);
            this.arrivalTime = arrivalTime;
        }
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        if (departureTime != null) {
            ValidationHelpers.validateTimeAgainstPresent(departureTime, ATTRIBUTE_NAME_DEPARTURE_TIME);
            if (arrivalTime != null){
                ValidationHelpers.validateTimeAgainstTime(arrivalTime, departureTime,
                        ATTRIBUTE_NAME_ARRIVAL_TIME, ATTRIBUTE_NAME_DEPARTURE_TIME);
            }
            this.departureTime = departureTime;
        }
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

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public LocationType getType() {
        return type;
    }

    private void setType(){
        if (arrivalTime == null){
            type = LocationType.START;
        } else if (departureTime == null) {
            type = LocationType.END;
        }else {
            type = LocationType.INTERMEDIATE;
        }
    }

    @Override
    public String toString() {
        return switch(getType()){
            case START -> String.format(LOCATION_TO_STRING_BASE + LOCATION_TO_STRING_DEPARTING,
                    getName(), getDepartureTime().format(formatter));
            case INTERMEDIATE -> String.format(LOCATION_TO_STRING_BASE + LOCATION_TO_STRING_ARRIVING
                            + LOCATION_TO_STRING_DEPARTING,
                    getName(),
                    getArrivalTime().format(formatter),
                    getDepartureTime().format(formatter));
            case END -> String.format(LOCATION_TO_STRING_BASE + LOCATION_TO_STRING_ARRIVING, getName(), getArrivalTime().format(formatter));
        };
    }
}
