package com.company.oop.logistics.commands.listing;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.modelservices.contracts.RouteService;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.services.AssignmentService;

import java.util.List;
import java.util.stream.Collectors;

public class ListRoutesCommand implements Command {
    public static final String ERROR_PARAMETERS_AMOUNT = "This command requires exactly 0 or 1 parameters.";
    public static final String ACTIVE_MODIFIER = "active";
    public static final String ERROR_INVALID_MODIFIER = "This parameter can only be a modifier: active";
    public static final String MESSAGE_NO_ROUTES = "There are no routes matching the criteria";
    public static final String SEPARATOR = "===";

    private final RouteService routeService;
    private final LocationService locationService;
    private final AssignmentService assignmentService;

    private boolean onlyActive = false;

    public ListRoutesCommand(RouteService routeService, LocationService locationService, AssignmentService assignmentService) {
        this.routeService = routeService;
        this.locationService = locationService;
        this.assignmentService = assignmentService;
    }

    @Override
    public String execute(List<String> parameters) {
        parseParameters(parameters);
        StringBuilder output = new StringBuilder();
        List<DeliveryRoute> routesToItterate;
        if (onlyActive){
            routesToItterate = routeService.getRoutesInProgress();
        }else{
            routesToItterate = routeService.getAllRoutes();
        }
        if (routesToItterate.isEmpty()){
            output.append(MESSAGE_NO_ROUTES);
        }
        for (DeliveryRoute route: routesToItterate){
            output.append(SEPARATOR).append(System.lineSeparator());
            City originName = locationService.getLocationById(route.getOrigin()).getName();
            City destinationName = locationService.getLocationById(route.getDestination()).getName();
            output.append(String.format("Route id: %d - from %s to %s\n", route.getId(), originName,
                    destinationName));
            try{
                output.append(String.format("""
                          - Assigned truck id: %s
                          - Current load: %s
                          - Free capacity: %s
                          - Total distance: %d
                          """,
                        route.getAssignedVehicleId(),
                        assignmentService.getMaxLoad(route.getId(), originName, destinationName),
                        assignmentService.getFreeCapacity(route.getId(), originName, destinationName),
                        route.getDistance()));
            }catch (RuntimeException e){
                output.append(" - No assigned truck yet\n");
            }
            output.append(route.getLocations().stream()
                            .map(locationService::getLocationById)
                            .map(Object::toString)
                            .collect(Collectors.joining(""))
                    );
        }
        if (routesToItterate.isEmpty()){
            output.append(MESSAGE_NO_ROUTES);
        }else{
            output.append(SEPARATOR);
        }
        return output.toString();
    }

    private void parseParameters(List<String> parameters){
        if (parameters.isEmpty()){
            return;
        }
        if (parameters.size() != 1){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        if (parameters.get(0).equalsIgnoreCase(ACTIVE_MODIFIER)){
            onlyActive = true;
        }else{
            throw new IllegalArgumentException(ERROR_INVALID_MODIFIER);
        }
    }
}
