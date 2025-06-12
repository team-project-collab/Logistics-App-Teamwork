package com.company.oop.logistics.commands.creation;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.ObjectRepository;
import com.company.oop.logistics.models.LocationImpl;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.parcing.ParsingHelpers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateRouteCommand implements Command {
    public static final int EXPECTED_NUMBER_OF_PARAMETERS = 3;
    public static final String ERROR_PARAMETERS_AMOUNT = String.format("This command requires at least %d parameters.",
            EXPECTED_NUMBER_OF_PARAMETERS);
    private static final String INVALID_CITY = "City %s not supported.";
    private LocalDateTime startTime;
    private LocalDateTime currentTime;
    private ArrayList<City> cities;
    private ArrayList<Location> locations = new ArrayList<>();
    private final ObjectRepository objectRepository;

    public CreateRouteCommand(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    @Override
    public String execute(List<String> parameters) {
        if (parameters.size() < EXPECTED_NUMBER_OF_PARAMETERS){
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }

        parseParameters(parameters);
        currentTime = startTime;

        for (City city: cities){
            //TODO: Calculate travel time
            int timeToTravel = 5*60*60;
            currentTime = currentTime.plusSeconds(timeToTravel);

            locations.add(new LocationImpl( city, currentTime.plusSeconds(-timeToTravel), currentTime));
        }
        DeliveryRoute createdRoute = objectRepository.createDeliveryRoute(startTime, locations);
        return String.format("Created new route with id: %d", createdRoute.getId());
    }

    private void parseParameters(List<String> parameters){
        startTime = ParsingHelpers.tryParseLocalDateTime(parameters.get(0), "start time");
        cities = new ArrayList<>();
        for (int i = 1; i < parameters.size(); i++){
            cities.add(ParsingHelpers.tryParseEnum(parameters.get(i), City.class, INVALID_CITY));
        }
    }
}
