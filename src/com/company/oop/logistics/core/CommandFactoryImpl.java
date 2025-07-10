package com.company.oop.logistics.core;

import com.company.oop.logistics.commands.*;
import com.company.oop.logistics.commands.assign.*;
import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.commands.creation.*;
import com.company.oop.logistics.commands.listing.*;
import com.company.oop.logistics.core.contracts.*;
import com.company.oop.logistics.modelservices.contracts.*;
import com.company.oop.logistics.services.AssignmentService;
import com.company.oop.logistics.services.AssignmentServiceImpl;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

public class CommandFactoryImpl implements CommandFactory {

    private static final String INVALID_COMMAND = "Invalid command name: %s!";
    private final LocationService locationService;
    private final RouteService routeService;
    private final VehicleService vehicleService;
    private final DeliveryPackageService deliveryPackageService;
    private final CustomerService customerService;
    private final AssignmentService assignmentService;

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
                return new ListRoutesCommand(routeService, locationService, assignmentService);
            case ASSIGNVEHICLETOROUTE:
                return new AssignVehicleToRouteCommand(assignmentService);
            case CREATETRUCK:
                return new CreateTruckCommand(vehicleService);
            case CREATEDELIVERYPACKAGE:
                return new CreateDeliveryPackageCommand(deliveryPackageService,customerService);
            case FINDROUTESSERVICINGSTARTANDEND:
                return new FindRoutesServicingStartAndEndCommand(routeService, locationService, assignmentService);
            case ASSIGNPACKAGE:
                return new AssignPackageCommand(assignmentService);
            case CREATECUSTOMERCONTACTINFO:
                return new CreateCustomerContactInfo(customerService);
            case GETPACKAGESTATE:
                return new GetPackageStateCommand(deliveryPackageService);
            case GETUNASSIGNEDPACKAGES:
                return new GetUnassignedPackagesCommand(deliveryPackageService);
            case BULKASSIGNPACKAGES:
                return new BulkAssignPackagesCommand(assignmentService);
            case LISTVEHICLES:
                return new ListVehiclesCommand(vehicleService, locationService);
        }
        return null;
    }
}
