package com.company.oop.logistics.utils.misc;

import com.company.oop.logistics.modelservices.contracts.VehicleService;
import com.company.oop.logistics.exceptions.custom.LimitBreak;
import com.company.oop.logistics.models.TruckImpl;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.models.enums.TruckName;

import java.util.List;
import java.util.Random;

public abstract class InitializeTrucks {
    public static void execute(VehicleService vehicleService){
        setIds(vehicleService.getAllVehicles());
        createTrucks(vehicleService);
    }

    private static void setIds(List<Truck> vehicles) {
        int maxScaniaId = vehicles.stream().
                filter(t -> t.getTruckName().equals(TruckName.SCANIA)).
                mapToInt(Truck::getId).max().orElse(1000) + 1;
        int maxManId = vehicles.stream().
                filter(t -> t.getTruckName().equals(TruckName.MAN))
                .mapToInt(Truck::getId).max().orElse(1010) + 1;
        int maxActrosId = vehicles.stream().
                filter(t -> t.getTruckName().equals(TruckName.ACTROS)).
                mapToInt(Truck::getId).max().orElse(1025) + 1;

        TruckImpl.setIds(maxScaniaId, maxManId, maxActrosId);
    }

    private static void createTrucks(VehicleService vehicleService){
        for (TruckName truckName: TruckName.values()) {
            boolean needsMore = true;
            while (needsMore) {
                try {
                    vehicleService.createVehicle(truckName.toString(), generateRandomCity());
                } catch (LimitBreak e) {
                    needsMore = false;
                }
            }
        }
    }

    private static City generateRandomCity(){
        return City.values()[new Random().nextInt(City.values().length)];
    }
}
