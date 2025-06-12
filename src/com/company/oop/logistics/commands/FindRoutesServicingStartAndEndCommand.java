package com.company.oop.logistics.commands;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.ObjectRepository;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.parcing.ParsingHelpers;

import java.util.ArrayList;
import java.util.List;

public class FindRoutesServicingStartAndEndCommand implements Command {
    private static final int EXPECTED_NUMBER_OF_PARAMETERS = 2;
    private static final String MESSAGE_LIST_ROUTES = "Routes ids servicing this start and end: %s";
    private static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters",
            EXPECTED_NUMBER_OF_PARAMETERS);
    private final ObjectRepository objectRepository;
    private City origin;
    private City destination;

    public FindRoutesServicingStartAndEndCommand(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        parseParameters(parameters);
        ArrayList<Integer> result = objectRepository.findRoutesServicingStartAndEnd(origin, destination);
        return String.format(MESSAGE_LIST_ROUTES, result.toString());
    }
    private void parseParameters(List<String> parameters){
        origin = ParsingHelpers.tryParseEnum(parameters.get(0), City.class, "city name");
        destination = ParsingHelpers.tryParseEnum(parameters.get(1), City.class, "city name");
    }
}
