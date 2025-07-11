package com.company.oop.logistics.commands.creation;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.modelservices.contracts.VehicleService;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

import java.util.List;

public class CreateTruckCommand implements Command {
    public static final int EXPECTED_NUMBER_OF_PARAMETERS = 2;
    public static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters.",
            EXPECTED_NUMBER_OF_PARAMETERS);
    private static final String INVALID_CITY = "City %s not supported.";

    private final VehicleService vehicleService;
    private String truckName;
    private City cityName;

    public CreateTruckCommand( VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }
    @Override

    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        parseParameters(parameters);
        Truck createdVehicle = vehicleService.createVehicle(truckName, cityName);
        return String.format("Created truck %s with id %d\n", createdVehicle.getTruckName(), createdVehicle.getId());
    }

    private void parseParameters(List<String> parameters){
        truckName = parameters.get(0);
        cityName = ParsingHelpers.tryParseEnum(parameters.get(1), City.class, String.format(INVALID_CITY, parameters.get(1)));
    }
}
