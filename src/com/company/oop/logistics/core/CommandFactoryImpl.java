package com.company.oop.logistics.core;

import com.company.oop.logistics.commands.assign.AssignVehicleToRouteCommand;
import com.company.oop.logistics.commands.CommandType;
import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.commands.creation.CreateLocationCommand;
import com.company.oop.logistics.commands.creation.CreateRouteCommand;
import com.company.oop.logistics.commands.creation.CreateTruckCommand;
import com.company.oop.logistics.commands.listing.ListLocationsCommand;
import com.company.oop.logistics.commands.listing.ListRoutesCommand;
import com.company.oop.logistics.core.contracts.CommandFactory;
import com.company.oop.logistics.core.contracts.ObjectRepository;
import com.company.oop.logistics.utils.parcing.ParsingHelpers;

public class CommandFactoryImpl implements CommandFactory {

    private static final String INVALID_COMMAND = "Invalid command name: %s!";

    @Override
    public Command createCommandFromCommandName(String commandTypeValue, ObjectRepository objectRepository) {
        CommandType commandType = ParsingHelpers.tryParseEnum(commandTypeValue, CommandType.class, String.format(INVALID_COMMAND, commandTypeValue));

        switch (commandType) {
            case CREATELOCATION:
                return new CreateLocationCommand(objectRepository);
            case LISTLOCATIONS:
                return new ListLocationsCommand(objectRepository);
            case CREATEROUTE:
                return new CreateRouteCommand(objectRepository);
            case LISTROUTES:
                return new ListRoutesCommand(objectRepository);
            case ASSIGNVEHICLETOROUTE:
                return new AssignVehicleToRouteCommand(objectRepository);
            case CREATETRUCK:
                return new CreateTruckCommand(objectRepository);
        }
        return null;
    }
}
