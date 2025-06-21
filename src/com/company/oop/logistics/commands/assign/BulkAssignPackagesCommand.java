package com.company.oop.logistics.commands.assign;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.DeliveryPackageService;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BulkAssignPackagesCommand implements Command {
    private static final int EXPECTED_NUMBER_OF_PARAMETERS = 1;
    private static final String MESSAGE_ADDED_PACKAGES_TO_ROUTE = "%d packages added to route %d";
    private static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters",
            EXPECTED_NUMBER_OF_PARAMETERS);
    
    private final DeliveryPackageService deliveryPackageService;
    private int deliveryRouteId;

    public BulkAssignPackagesCommand(DeliveryPackageService deliveryPackageService) {
        this.deliveryPackageService = deliveryPackageService;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        parseParameters(parameters);

        LocalDateTime currentTime = LocalDateTime.now();
        ArrayList<DeliveryPackage> unassignedPackages = deliveryPackageService.getUnassignedPackages(currentTime);
        
        if (unassignedPackages.isEmpty()) {
            return String.format("No unassigned packages to assign to route %d", deliveryRouteId);
        }

        deliveryPackageService.bulkAssignPackages(deliveryRouteId, currentTime);
        //If someone has an idea how to make this better, feel free to do so
        ArrayList<DeliveryPackage> unassignedPackagesAfterRouteCheck = deliveryPackageService.getUnassignedPackages(currentTime);
        return String.format(MESSAGE_ADDED_PACKAGES_TO_ROUTE, unassignedPackages.size() - unassignedPackagesAfterRouteCheck.size()
                , deliveryRouteId);
    }
    
    private void parseParameters(List<String> parameters){
        deliveryRouteId = ParsingHelpers.tryParseInteger(parameters.get(0), "delivery route id");
    }
}
