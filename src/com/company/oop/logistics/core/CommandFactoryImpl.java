package com.company.oop.logistics.core;

import com.company.oop.logistics.commands.*;
import com.company.oop.logistics.commands.assign.*;
import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.commands.creation.*;
import com.company.oop.logistics.commands.listing.*;
import com.company.oop.logistics.core.contracts.*;
import com.company.oop.logistics.modelservices.contracts.*;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CommandFactoryImpl implements CommandFactory {

    private static final String INVALID_COMMAND = "Invalid command name: %s!";
    private final LocationService locationService;
    private final RouteService routeService;
    private final VehicleService vehicleService;
    private final DeliveryPackageService deliveryPackageService;
    private final CustomerService customerService;
    private final AssignmentService assignmentService;
    private final Map<CommandType, Supplier<Command>> commandRegistry = new HashMap<>();;

    public CommandFactoryImpl(LocationService locationService,
                              RouteService routeService,
                              VehicleService vehicleService,
                              DeliveryPackageService deliveryPackageService,
                              CustomerService customerService,
                              AssignmentService assignmentService) {
        this.locationService = locationService;
        this.routeService = routeService;
        this.vehicleService = vehicleService;
        this.deliveryPackageService = deliveryPackageService;
        this.customerService = customerService;
        this.assignmentService = assignmentService;

        registerCommands();
    }

    private void registerCommands() {
        commandRegistry.put(CommandType.CREATELOCATION, () -> new CreateLocationCommand(locationService));
        commandRegistry.put(CommandType.LISTLOCATIONS, () -> new ListLocationsCommand(locationService));
        commandRegistry.put(CommandType.CREATEROUTE, () -> new CreateRouteCommand(routeService));
        commandRegistry.put(CommandType.LISTROUTES, () -> new ListRoutesCommand(routeService, locationService, assignmentService));
        commandRegistry.put(CommandType.ASSIGNVEHICLETOROUTE, () -> new AssignVehicleToRouteCommand(assignmentService));
        commandRegistry.put(CommandType.CREATETRUCK, () -> new CreateTruckCommand(vehicleService));
        commandRegistry.put(CommandType.CREATEDELIVERYPACKAGE, () -> new CreateDeliveryPackageCommand(deliveryPackageService, customerService));
        commandRegistry.put(CommandType.FINDROUTESSERVICINGSTARTANDEND, () -> new FindRoutesServicingStartAndEndCommand(routeService, locationService, assignmentService));
        commandRegistry.put(CommandType.ASSIGNPACKAGE, () -> new AssignPackageCommand(assignmentService));
        commandRegistry.put(CommandType.CREATECUSTOMERCONTACTINFO, () -> new CreateCustomerContactInfo(customerService));
        commandRegistry.put(CommandType.GETPACKAGESTATE, () -> new GetPackageStateCommand(deliveryPackageService));
        commandRegistry.put(CommandType.GETUNASSIGNEDPACKAGES, () -> new GetUnassignedPackagesCommand(deliveryPackageService));
        commandRegistry.put(CommandType.BULKASSIGNPACKAGES, () -> new BulkAssignPackagesCommand(assignmentService));
        commandRegistry.put(CommandType.LISTVEHICLES, () -> new ListVehiclesCommand(vehicleService, locationService));
    }

    @Override
    public Command createCommandFromCommandName(String commandTypeValue) {
        CommandType commandType = ParsingHelpers.tryParseEnum(
                commandTypeValue, CommandType.class,
                String.format(INVALID_COMMAND, commandTypeValue)
        );

        Supplier<Command> commandSupplier = commandRegistry.get(commandType);
        if (commandSupplier == null) {
            throw new IllegalArgumentException(String.format(INVALID_COMMAND, commandTypeValue));
        }

        return commandSupplier.get();
    }

    }

