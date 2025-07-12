package com.company.oop.logistics.commands.assign;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

import java.util.List;

public class BulkAssignPackagesCommand implements Command {
    private static final int EXPECTED_NUMBER_OF_PARAMETERS = 1;
    private static final String MESSAGE_ADDED_PACKAGES_TO_ROUTE = "%d packages added to route %d";
    private static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters",
            EXPECTED_NUMBER_OF_PARAMETERS);

    private final AssignmentService assignmentService;
    private int deliveryRouteId;

    public BulkAssignPackagesCommand(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        parseParameters(parameters);
        int assignedPackages = assignmentService.bulkAssignPackages(deliveryRouteId);
        return String.format(MESSAGE_ADDED_PACKAGES_TO_ROUTE, assignedPackages, deliveryRouteId);
    }
    
    private void parseParameters(List<String> parameters){
        deliveryRouteId = ParsingHelpers.tryParseInteger(parameters.get(0), "delivery route id");
    }
}
