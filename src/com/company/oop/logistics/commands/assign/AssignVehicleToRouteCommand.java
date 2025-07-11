package com.company.oop.logistics.commands.assign;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

import java.util.List;

public class AssignVehicleToRouteCommand implements Command {

    private static final int EXPECTED_NUMBER_OF_PARAMETERS = 2;
    private static final String MESSAGE_VEHICLE_ADDED_TO_ROUTE = "Vehicle %d added to route %d";
    private static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters",
            EXPECTED_NUMBER_OF_PARAMETERS);
    private final AssignmentService assignmentService;
    private int vehicleId;
    private int deliveryRouteId;

    public AssignVehicleToRouteCommand(AssignmentService assignmentService){
        this.assignmentService = assignmentService;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        parseParameters(parameters);
        assignmentService.assignVehicleToRoute(vehicleId, deliveryRouteId);
        return String.format(MESSAGE_VEHICLE_ADDED_TO_ROUTE, vehicleId, deliveryRouteId);
    }

    private void parseParameters(List<String> parameters){
        vehicleId = ParsingHelpers.tryParseInteger(parameters.get(0), "vehicle id");
        deliveryRouteId = ParsingHelpers.tryParseInteger(parameters.get(1), "route id");
    }
}
