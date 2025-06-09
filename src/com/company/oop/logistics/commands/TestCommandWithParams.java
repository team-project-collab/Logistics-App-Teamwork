package com.company.oop.logistics.commands;

import com.company.oop.logistics.commands.contracts.Command;

import java.util.List;

public class TestCommandWithParams implements Command {

    public static final int EXPECTED_NUMBER_OF_PARAMETERS = 1;
    public static final String ERROR_PARAMETERS_AMOUNT = "This command requires exactly 1 parameter.";

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        return String.format("Executing test command with parameter %s", parameters.get(0));
    }
}
