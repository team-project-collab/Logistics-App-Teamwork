package com.company.oop.logistics.core;

import com.company.oop.logistics.commands.FindRoutesServicingStartAndEndCommand;
import com.company.oop.logistics.commands.GetPackageStateCommand;
import com.company.oop.logistics.commands.assign.AssignPackageCommand;
import com.company.oop.logistics.commands.assign.AssignVehicleToRouteCommand;
import com.company.oop.logistics.commands.CommandType;
import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.commands.creation.*;
import com.company.oop.logistics.commands.listing.ListLocationsCommand;
import com.company.oop.logistics.commands.listing.ListRoutesCommand;
import com.company.oop.logistics.core.contracts.*;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

public class CommandFactoryImpl implements CommandFactory {

    private static final String INVALID_COMMAND = "Invalid command name: %s!";
    private final LocationService locationService;
    private final RouteService routeService;
    private final VehicleService vehicleService;
    private final DeliveryPackageService deliveryPackageService;
    private final CustomerService customerService;

    public CommandFactoryImpl(LocationService locationService, RouteService routeService, VehicleService vehicleService, DeliveryPackageService deliveryPackageService, CustomerService customerService) {
        this.locationService = locationService;
        this.routeService = routeService;
        this.vehicleService = vehicleService;
        this.deliveryPackageService = deliveryPackageService;
        this.customerService = customerService;
    }

    @Override
    public Command createCommandFromCommandName(String commandTypeValue) {
        CommandType commandType = ParsingHelpers.tryParseEnum(commandTypeValue, CommandType.class, String.format(INVALID_COMMAND, commandTypeValue));

        switch (commandType) {
            case CREATELOCATION:
                return new CreateLocationCommand(locationService);
            case LISTLOCATIONS:
                return new ListLocationsCommand(locationService);
            case CREATEROUTE:
                return new CreateRouteCommand(routeService);
            case LISTROUTES:
                return new ListRoutesCommand(routeService);
            case ASSIGNVEHICLETOROUTE:
                return new AssignVehicleToRouteCommand(routeService);
            case CREATETRUCK:
                return new CreateTruckCommand(vehicleService);
            case CREATEDELIVERYPACKAGE:
                return new CreateDeliveryPackageCommand(deliveryPackageService,customerService);
            case FINDROUTESSERVICINGSTARTANDEND:
                return new FindRoutesServicingStartAndEndCommand(routeService);
            case ASSIGNPACKAGE:
                return new AssignPackageCommand(deliveryPackageService);
            case CREATECUSTOMERCONTACTINFO:
                return new CreateCustomerContactInfo(customerService);
            case GETPACKAGESTATECOMMAND:
                return new GetPackageStateCommand(deliveryPackageService);
        }
        return null;
    }
}
