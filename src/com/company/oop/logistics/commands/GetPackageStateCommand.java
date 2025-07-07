package com.company.oop.logistics.commands;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.DeliveryPackageService;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

import java.time.LocalDateTime;
import java.util.List;

public class GetPackageStateCommand implements Command {
    private static final int EXPECTED_NUMBER_OF_PARAMETERS = 2;
    private static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters",
            EXPECTED_NUMBER_OF_PARAMETERS);
    private static final String INVALID_CITY = "City %s not supported.";

    private final DeliveryPackageService deliveryPackageService;
    private int packageId;
    private LocalDateTime time;

    public GetPackageStateCommand(DeliveryPackageService deliveryPackageService) {
        this.deliveryPackageService = deliveryPackageService;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        parseParameters(parameters);

        return String.format("""
                Information for package id: %d:
                %s
                """, packageId, deliveryPackageService.getPackageState(packageId, time));
    }
    private void parseParameters(List<String> parameters){
        packageId = ParsingHelpers.tryParseInteger(parameters.get(0), "package id");
        time = ParsingHelpers.tryParseLocalDateTime(parameters.get(1), "date time");
    }
}
