package com.company.oop.logistics.commands.listing;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.modelservices.contracts.LocationService;

import java.util.List;
import java.util.stream.Collectors;

public class ListLocationsCommand implements Command {
    private final LocationService locationService;

    public ListLocationsCommand( LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public String execute(List<String> parameters) {
        return locationService.getLocations().stream()
                .map(Object::toString)
                .collect(Collectors.joining(""));
    }
}
