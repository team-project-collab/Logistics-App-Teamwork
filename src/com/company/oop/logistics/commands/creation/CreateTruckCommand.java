package com.company.oop.logistics.commands.creation;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.ObjectRepository;
import com.company.oop.logistics.models.contracts.Truck;
import java.util.List;

public class CreateTruckCommand implements Command {
    public static final int EXPECTED_NUMBER_OF_PARAMETERS = 1;
    public static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters.",
            EXPECTED_NUMBER_OF_PARAMETERS);
    private static final String INVALID_TRUCK = "Truck %s not supported.";

    private final ObjectRepository objectRepository;
    private String truckName;

    public CreateTruckCommand(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }
    @Override

    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        parseParameters(parameters);
        Truck createdVehicle = objectRepository.createVehicle(truckName);
        return String.format("Created truck %s with id %d\n", createdVehicle.getTruckName(), createdVehicle.getId());
    }

    private void parseParameters(List<String> parameters){
        truckName = parameters.get(0);
    }
}
