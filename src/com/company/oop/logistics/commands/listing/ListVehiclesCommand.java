package com.company.oop.logistics.commands.listing;

import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.modelservices.contracts.VehicleService;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.utils.misc.LocationInfo;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

import java.time.LocalDateTime;
import java.util.List;

public class ListVehiclesCommand implements Command {
    public static final String ERROR_PARAMETERS_AMOUNT = "This command requires exactly 0 or 1 parameters.";
    public static final String FREE_MODIFIER = "free";
    public static final String ERROR_INVALID_MODIFIER = "This parameter can only be a city code or a modifier: free.";

    private final VehicleService vehicleService;
    private final LocationService locationService;
    private City cityName;
    private boolean onlyFree = false;

    public ListVehiclesCommand(VehicleService vehicleService, LocationService locationService) {
        this.vehicleService = vehicleService;
        this.locationService = locationService;
    }

    @Override
    public String execute(List<String> parameters) {
        LocalDateTime now = LocalDateTime.now();
        StringBuilder output = new StringBuilder();
        parseParameters(parameters);
        List<Truck> filteredVehicles = vehicleService.getAllVehicles().stream()
                .filter(v -> (cityName == null) ||
                                    vehicleService.getCurrentLocation(v.getId(), now).getName().equals(cityName))
                .filter(v -> (!onlyFree || vehicleService.isVehicleFree(v.getId(), now)))
                .toList();
        for (Truck truck : filteredVehicles) {
            LocationInfo locationInfo = new LocationInfo(locationService, truck.getLocationIds(), now);
            output.append("===\n");
            output.append(String.format("""
                            Truck id: %d, Brand: %s, Max load: %d
                            %s
                            """,
                    truck.getId(),
                    truck.getTruckName(),
                    truck.getCapacity(),
                    locationInfo.getTruckStatus()
            ));
        }
        if (filteredVehicles.isEmpty()){
            output.append("No vehicles found with this search");
        }else {
            output.append("===");
        }
        return output.toString();
    }
    private void parseParameters(List<String> parameters){
        if (parameters == null || parameters.isEmpty()){
            return;
        }
        if (parameters.size() == 1){
            try{
                cityName = ParsingHelpers.tryParseEnum(parameters.get(0), City.class, "city name");
            }catch (RuntimeException e){
                if (parameters.get(0).equalsIgnoreCase(FREE_MODIFIER)){
                    onlyFree = true;
                }else{
                    throw new IllegalArgumentException(ERROR_INVALID_MODIFIER);
                }
            }
        }
        else{
            throw new IllegalArgumentException(ERROR_PARAMETERS_AMOUNT);
        }
    }
}
