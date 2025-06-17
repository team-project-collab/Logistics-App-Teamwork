package com.company.oop.logistics.commands.creation;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.CustomerService;
import com.company.oop.logistics.models.CustomerContactInfo;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.parcing.ParsingHelpers;

import java.util.List;

public class CreateCustomerContactInfo implements Command {
    public static final int EXPECTED_NUMBER_OF_PARAMETERS = 4;
    public static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires exactly %d parameters.",
            EXPECTED_NUMBER_OF_PARAMETERS);
    private static final String INVALID_CITY = "City %s not supported.";

    private String fullName;
    private String phoneNumber;
    private String email;
    private City address;
    private final CustomerService customerService;



    public CreateCustomerContactInfo(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() != EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }

        parseParameters(parameters);

        CustomerContactInfo createdCustomer =customerService.createCustomerContactInfo(fullName, phoneNumber, email, address);
        return String.format("Created new customer with id: %d", createdCustomer.getId());
    }

    private void parseParameters(List<String> parameters){
        //TODO: validate number, email
        fullName = parameters.get(0);
        phoneNumber = parameters.get(1);
        email = parameters.get(2);
        address = ParsingHelpers.tryParseEnum(parameters.get(3), City.class, String.format(INVALID_CITY, parameters.get(0)));
    }
}

