package com.company.oop.logistics.commands.listing;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.LocationService;
import com.company.oop.logistics.models.contracts.Location;

import java.util.List;

public class ListLocationsCommand implements Command {
    private final LocationService locationService;

    public ListLocationsCommand( LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public String execute(List<String> parameters) {
        StringBuilder output = new StringBuilder();
        for (Location location: locationService.getLocations()){
            output.append("===\n");
            output.append(String.format(" Arrival time: %s\n", location.getArrivalTime()));
            output.append(String.format(" Departure time: %s\n", location.getDepartureTime()));
        }
        return output.toString();
    }
}
