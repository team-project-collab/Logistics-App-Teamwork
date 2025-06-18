package com.company.oop.logistics.commands;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.RouteService;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

import java.util.ArrayList;
import java.util.List;

public class FindRoutesServicingStartAndEndCommand implements Command {
    private static final int EXPECTED_NUMBER_OF_PARAMETERS = 2;
    private static final String MESSAGE_LIST_ROUTES = "Routes servicing route %s to %s: %s. Loaded capacity: %s\n";
    private static final String MESSAGE_NO_ROUTES = "There are no routes servicing %s to %s";
    private static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters",
            EXPECTED_NUMBER_OF_PARAMETERS);
    private static final String INVALID_CITY = "City %s not supported.";

    private final RouteService routeService;
    private City origin;
    private City destination;

    public FindRoutesServicingStartAndEndCommand(RouteService routeService) {
        this.routeService = routeService;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        parseParameters(parameters);
        ArrayList<Integer> result = routeService.findRoutesServicingStartAndEnd(origin, destination);
        if (result.isEmpty()){
            return String.format(MESSAGE_NO_ROUTES, origin, destination);
        }
        String returnString = "";
        for (int i = 0; i < result.size(); i++) {
            returnString += String.format(MESSAGE_LIST_ROUTES, origin, destination, result.get(i), routeService.getRouteById(result.get(i)).getMaxLoad(origin, destination));
        }
        return returnString;
    }
    private void parseParameters(List<String> parameters){
        origin = ParsingHelpers.tryParseEnum(parameters.get(0), City.class, String.format(INVALID_CITY, parameters.get(0)));
        destination = ParsingHelpers.tryParseEnum(parameters.get(1), City.class, String.format(INVALID_CITY, parameters.get(1)));
    }
}
