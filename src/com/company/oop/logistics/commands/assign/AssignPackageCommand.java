package com.company.oop.logistics.commands.assign;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.DeliveryPackageService;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

import java.util.List;

public class AssignPackageCommand implements Command {
    private static final int EXPECTED_NUMBER_OF_PARAMETERS = 2;
    private static final String MESSAGE_PACKAGE_ADDED_TO_ROUTE = "Package %d added to route %d";
    private static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters",
            EXPECTED_NUMBER_OF_PARAMETERS);
    private final DeliveryPackageService deliveryPackageService;
    private int packageId;
    private int deliveryRouteId;

    public AssignPackageCommand(DeliveryPackageService deliveryPackageService) {
        this.deliveryPackageService = deliveryPackageService;

    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        parseParameters(parameters);
        deliveryPackageService.assignPackage(packageId, deliveryRouteId);
        return String.format(MESSAGE_PACKAGE_ADDED_TO_ROUTE, packageId ,deliveryRouteId);
    }
    private void parseParameters(List<String> parameters){
        packageId = ParsingHelpers.tryParseInteger(parameters.get(0), "package id");
        deliveryRouteId = ParsingHelpers.tryParseInteger(parameters.get(1), "delivery route id");
    }
}
