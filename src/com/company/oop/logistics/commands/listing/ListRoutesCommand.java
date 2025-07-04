package com.company.oop.logistics.commands.listing;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.LocationService;
import com.company.oop.logistics.core.contracts.RouteService;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;

import java.util.List;

public class ListRoutesCommand implements Command {
    private final RouteService routeService;
    private final LocationService locationService;
    public ListRoutesCommand(RouteService routeService, LocationService locationService) {
        this.routeService = routeService;
        this.locationService = locationService;
    }

    @Override
    public String execute(List<String> parameters) {
        StringBuilder output = new StringBuilder();
        for (DeliveryRoute route: routeService.getRoutes()){
            output.append("===\n");
            City originName = locationService.getLocationById(route.getOrigin()).getName();
            City destinationName = locationService.getLocationById(route.getDestination()).getName();
            output.append(String.format(" Route id: %d - from %s to %s\n", route.getId(), originName,
                    destinationName));
            try{
                output.append(String.format("  Assigned truck %s\n", route.getAssignedVehicleId()));
            }catch (RuntimeException e){
                output.append("  No assigned truck yet\n");
            }
            for (int locationId: route.getLocations()) {
                Location location = locationService.getLocationById(locationId);
                output.append(String.format("  === Destination: %s at %s to %s\n", location.getName(),
                        location.getArrivalTime(), location.getDepartureTime()));
            }
        }
        return output.toString();
    }
}
