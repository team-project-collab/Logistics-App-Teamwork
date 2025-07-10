package com.company.oop.logistics.commands;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.modelservices.contracts.DeliveryPackageService;
import com.company.oop.logistics.models.contracts.DeliveryPackage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GetUnassignedPackagesCommand implements Command {
    private static final int EXPECTED_NUMBER_OF_PARAMETERS = 0;
    private static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters",
            EXPECTED_NUMBER_OF_PARAMETERS);
    public static final String MESSAGE_NO_PACKAGES = "There are no unassigned packages.";
    public static final String MESSAGE_LIST_PACKAGES = "Here is the list of unassigned packages:";

    private final DeliveryPackageService deliveryPackageService;
    private LocalDateTime time;

    public GetUnassignedPackagesCommand(DeliveryPackageService deliveryPackageService) {
        this.deliveryPackageService = deliveryPackageService;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        StringBuilder result = new StringBuilder();
        List<DeliveryPackage> unassignedPackages = deliveryPackageService.getUnassignedPackages();
        if (unassignedPackages.isEmpty()){
            result.append(MESSAGE_NO_PACKAGES);
        }else{
            result.append(MESSAGE_LIST_PACKAGES);
            result.append(System.lineSeparator());
        }
        result.append(
                unassignedPackages.stream().map(DeliveryPackage::toString).collect(Collectors.joining(System.lineSeparator()))
        );
        return result.toString();
    }

}
