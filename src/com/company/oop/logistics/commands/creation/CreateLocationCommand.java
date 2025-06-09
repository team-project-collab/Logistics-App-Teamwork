package com.company.oop.logistics.commands.creation;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.ObjectRepository;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.parcing.ParsingHelpers;

import java.time.LocalDateTime;
import java.util.List;

public class CreateLocationCommand implements Command {
    public static final int EXPECTED_NUMBER_OF_PARAMETERS = 3;
    public static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters.",
            EXPECTED_NUMBER_OF_PARAMETERS);
    private static final String INVALID_CITY = "City %s not supported.";

    private City location;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private final ObjectRepository objectRepository;

    public CreateLocationCommand(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }

        parseParameters(parameters);

        Location createdLocation = objectRepository.createLocation(location, arrivalTime, departureTime);
        return String.format("Created new location at %s from %s to %s", location, arrivalTime, departureTime);
    }

    private void parseParameters(List<String> parameters){
        location = ParsingHelpers.tryParseEnum(parameters.get(0), City.class, String.format(INVALID_CITY, parameters.get(0)));
        arrivalTime = ParsingHelpers.tryParseLocalDateTime(parameters.get(1), "arrival time");
        departureTime = ParsingHelpers.tryParseLocalDateTime(parameters.get(2), "departure time");
    }
}
