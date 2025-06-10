package com.company.oop.logistics.commands.listing;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.ObjectRepository;
import com.company.oop.logistics.models.contracts.Location;

import java.util.List;

public class ListLocationsCommand implements Command {
    private final ObjectRepository objectRepository;

    public ListLocationsCommand(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    @Override
    public String execute(List<String> parameters) {
        StringBuilder output = new StringBuilder();
        for (Location location: objectRepository.getLocations()){
            output.append("===\n");
            output.append(String.format(" Arrival time: %s\n", location.getArrivalTime()));
            output.append(String.format(" Departure time: %s\n", location.getDepartureTime()));
        }
        return output.toString();
    }
}
