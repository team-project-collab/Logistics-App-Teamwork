package com.company.oop.logistics.commands.creation;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.CustomerService;
import com.company.oop.logistics.core.contracts.DeliveryPackageService;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.contracts.DeliveryPackage;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.parcing.ParsingHelpers;

import java.util.List;

public class CreateDeliveryPackageCommand implements Command {
    public static final int EXPECTED_NUMBER_OF_PARAMETERS = 4;
    public static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters.",
            EXPECTED_NUMBER_OF_PARAMETERS);

    private City startLocation;
    private City endLocation;
    private double weightKg;
    private int customerContactInfoId;
    private final DeliveryPackageService deliveryPackageService;
    private final CustomerService customerService;

    public CreateDeliveryPackageCommand(DeliveryPackageService deliveryPackageService, CustomerService customerService) {

        this.deliveryPackageService= deliveryPackageService;
        this.customerService = customerService;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size()!=EXPECTED_NUMBER_OF_PARAMETERS) {
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
        parseParameters(parameters);

        CustomerContactInfo customerContactInfo = customerService.getCustomerContactById(customerContactInfoId);
        DeliveryPackage createdPackage=deliveryPackageService.createDeliveryPackage(startLocation, endLocation, weightKg, customerContactInfo);
        return String.format("Created new delivery package with id: %d",createdPackage.getId());
    }

    private void parseParameters(List<String> parameters) {
        startLocation = ParsingHelpers.tryParseEnum(parameters.get(0), City.class, "city name");
        endLocation = ParsingHelpers.tryParseEnum(parameters.get(1), City.class, "city name");
        weightKg = ParsingHelpers.tryParseDouble(parameters.get(2), "weight (kg)");
        customerContactInfoId = ParsingHelpers.tryParseInteger(parameters.get(3), "customer contact info id");
    }
}
