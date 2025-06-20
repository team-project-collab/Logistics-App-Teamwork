package com.company.oop.logistics.commands.listing;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.RouteService;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;

import java.util.List;

public class ListRoutesCommand implements Command {
    private final RouteService routeService;
    public ListRoutesCommand(RouteService routeService) {
        this.routeService = routeService;
    }

    @Override
    public String execute(List<String> parameters) {
        StringBuilder output = new StringBuilder();
        for (DeliveryRoute route: routeService.getRoutes()){
            output.append("===\n");
            output.append(String.format(" Route id: %d - from %s to %s\n", route.getId(), route.getOrigin().getName(),
                    route.getDestination().getName()));
            try{
                output.append(String.format("  Assigned truck %s\n", route.getAssignedVehicle().getId()));
            }catch (RuntimeException e){
                output.append("  No assigned truck yet\n");
            }
            for (Location location: route.getLocations()) {
                output.append(String.format("  === Destination: %s at %s to %s\n", location.getName(), location.getArrivalTime(), location.getDepartureTime()));
            }
        }
        return output.toString();
    }
}
