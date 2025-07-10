package com.company.oop.logistics.commands;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.modelservices.contracts.RouteService;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class FindRoutesServicingStartAndEndCommand implements Command {
    private static final int EXPECTED_NUMBER_OF_PARAMETERS = 2;
    private static final String MESSAGE_LIST_ROUTES = "Routes servicing route %s to %s: %s";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final String MESSAGE_NO_ROUTES = "There are no routes servicing %s to %s";
    private static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters",
            EXPECTED_NUMBER_OF_PARAMETERS);
    private static final String INVALID_CITY = "City %s not supported.";
    public static final String ROUTE_LIST_STRING = """
            ===
            Route id: %d;
             Origin: %s;
             Destination: %s;
             Departing %s at %s;
             Reaching destination %s at %s""";

    private final RouteService routeService;
    private final LocationService locationService;
    private final AssignmentService assignmentService;
    private City origin;
    private City destination;

    public FindRoutesServicingStartAndEndCommand(RouteService routeService, LocationService locationService, AssignmentService assignmentService) {
        this.routeService = routeService;
        this.locationService = locationService;
        this.assignmentService = assignmentService;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        parseParameters(parameters);
        List<DeliveryRoute> result = routeService.findRoutesServicingStartAndEnd(origin, destination);
        if (result.isEmpty()){
            return String.format(MESSAGE_NO_ROUTES, origin, destination);
        }
        StringBuilder resultString = new StringBuilder();
        for (DeliveryRoute route: result){
            resultString.append(String.format(ROUTE_LIST_STRING,
                    route.getId(),
                    locationService.getLocationById(route.getOrigin()).getName(),
                    locationService.getLocationById(route.getDestination()).getName(),
                    origin,
                    getLocationFromCity(origin, route).getDepartureTime().format(formatter),
                    destination,
                    getLocationFromCity(destination, route).getArrivalTime().format(formatter)));

            if (route.getAssignedVehicleId() != 0){
                double freeCapacity = assignmentService.getFreeCapacity(route.getId(), origin, destination);
                resultString.append(String.format("\n Free capacity: %.1f", freeCapacity));
            }else{
                resultString.append("\nNo assigned vehicle");
            }
        }


        return String.format(MESSAGE_LIST_ROUTES, origin, destination, resultString);
    }
    private void parseParameters(List<String> parameters){
        origin = ParsingHelpers.tryParseEnum(parameters.get(0), City.class, String.format(INVALID_CITY, parameters.get(0)));
        destination = ParsingHelpers.tryParseEnum(parameters.get(1), City.class, String.format(INVALID_CITY, parameters.get(1)));
    }

    private Location getLocationFromCity(City cityName, DeliveryRoute route){
        List <Location> locations = route.getLocations().stream().map(locationService::getLocationById).toList();
        return locations.stream()
                .filter(l -> l.getName().equals(cityName))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
