package com.company.oop.logistics.core;

import com.company.oop.logistics.core.contracts.ObjectRepository;
import com.company.oop.logistics.models.DeliveryRouteImpl;
import com.company.oop.logistics.models.LocationImpl;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ObjectRepositoryImpl implements ObjectRepository {
    private int nextId;

    List<Location> locations = new ArrayList<>();
    //TODO: Add lists for the objects we will store
    List<DeliveryRoute> routes = new ArrayList<>();


    public ObjectRepositoryImpl() {
        nextId = 0;
    }

    public Location createLocation(City name, LocalDateTime arrivalTime, LocalDateTime departureTime){
        return new LocationImpl(name, arrivalTime, departureTime);
    }

    public DeliveryRoute createDeliveryRoute(LocalDateTime startTime, ArrayList<Location> locations){
        DeliveryRoute route = new DeliveryRouteImpl(++nextId, startTime, locations);
        this.routes.add(route);
        return route;
    }

    @Override
    public List<Location> getLocations() {
        return locations;
    }

    @Override
    public List<DeliveryRoute> getRoutes(){
        return routes;
    }


}
