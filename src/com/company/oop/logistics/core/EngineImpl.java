package com.company.oop.logistics.core;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EngineImpl implements Engine {

    private static final String TERMINATION_COMMAND = "Exit";
    private static final String EMPTY_COMMAND_ERROR = "Command cannot be empty.";

    private final CommandFactory commandFactory;


    public EngineImpl() {
        LocationService locationService = new LocationServiceImpl();
        VehicleService vehicleService = new VehicleServiceImpl();
        RouteService routeService = new RouteServiceImpl(vehicleService,locationService);
        DeliveryPackageService deliveryPackageService = new DeliveryPackageServiceImpl(routeService);
        CustomerService customerService = new CustomerServiceImpl();
        this.commandFactory = new CommandFactoryImpl(locationService,routeService,vehicleService,deliveryPackageService,customerService);

    }


    public void start(){
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String inputLine = scanner.nextLine();
            if (inputLine.isBlank()) {
                System.out.println(EMPTY_COMMAND_ERROR);
                continue;
            }
            if (inputLine.equalsIgnoreCase(TERMINATION_COMMAND)) {
                break;
            }
            try {
                processCommand(inputLine);
            }
            catch (RuntimeException e){
                //This is a catch-all for unhandled errors
                System.out.println(e.getMessage());
            }
        }
    }

    private void processCommand(String inputLine) {
        String commandName = extractCommandName(inputLine);
        Command command;
        List<String> parameters = extractCommandParameters(inputLine);
        command = commandFactory.createCommandFromCommandName(commandName);
        String executionResult = command.execute(parameters);
        System.out.println(executionResult);
    }

    /**
     * Receives a full line and extracts the command to be executed from it.
     * For example, if the input line is "FilterBy Assignee John", the method will return "FilterBy".
     *
     * @param inputLine A complete input line
     * @return The name of the command to be executed
     */
    private String extractCommandName(String inputLine) {
        return inputLine.split(" ")[0];
    }

    /**
     * Receives a full line and extracts the parameters that are needed for the command to execute.
     * For example, if the input line is "FilterBy Assignee John",
     * the method will return a list of ["Assignee", "John"].
     *
     * @param inputLine A complete input line
     * @return A list of the parameters needed to execute the command
     */
    private List<String> extractCommandParameters(String inputLine) {
        String[] commandParts = inputLine.split(" ");
        List<String> parameters = new ArrayList<>();
        for (int i = 1; i < commandParts.length; i++) {
            parameters.add(commandParts[i]);
        }
        return parameters;
    }

}
