package com.company.oop.logistics.tests.modelserivices;

import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.contracts.DeliveryRoute;
import com.company.oop.logistics.models.contracts.Identifiable;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.LocationServiceImpl;
import com.company.oop.logistics.modelservices.RouteServiceImpl;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.modelservices.contracts.RouteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockLocationImpl;
import testingUtils.MockLocationServiceImpl;
import testingUtils.MockPersistenceManagerImpl;

import java.time.LocalDateTime;
import java.util.List;

public class RouteServiceTest {
    private final PersistenceManager mockPersistenceManager = new MockPersistenceManagerImpl();
    private final LocalDateTime validTime = LocalDateTime.now().plusHours(1);
    private final List<City> validCities = List.of(City.SYD, City.MEL, City.ADL);

    private RouteService routeService;

    @BeforeEach
    public void setUp(){
        LocationService locationService = new LocationServiceImpl(mockPersistenceManager);
        routeService = new RouteServiceImpl(mockPersistenceManager, locationService);
    }

    @Test
    public void createDeliveryRoute_Should_CreateValidRoute(){
        DeliveryRoute route = routeService.createDeliveryRoute(validTime, validCities);
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, route.getId()),
                () -> Assertions.assertEquals(3, route.getLocations().size())
        );
    }

    @Test
    public void createDeliveryRoute_Should_Throw_When_CitiesNotUnique(){
        List<City> citiesDuplicate = List.of(City.SYD, City.MEL, City.SYD);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> routeService.createDeliveryRoute(validTime, citiesDuplicate)
        );
    }

    @Test
    public void getRouteById_Should_ReturnCorrectRoute(){
        DeliveryRoute route1 = routeService.createDeliveryRoute(validTime, validCities);
        DeliveryRoute route2 = routeService.createDeliveryRoute(validTime, validCities);
        Assertions.assertAll(
                () -> Assertions.assertEquals(route1, routeService.getRouteById(1)),
                () -> Assertions.assertEquals(route2, routeService.getRouteById(2))
        );
    }

    @Test
    public void getRouteById_Should_Throw_When_RouteNotFound(){
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> routeService.getRouteById(0));
    }

    @Test
    public void getAllRoutes_Should_ReturnAllStoredRoutes(){
        DeliveryRoute route1 = routeService.createDeliveryRoute(validTime, validCities);
        DeliveryRoute route2 = routeService.createDeliveryRoute(validTime, validCities);
        Assertions.assertEquals(2, routeService.getAllRoutes().size());
    }

    @Test
    public void getRoutesInProgress_Should_Return_ActiveRoutes(){
        //route1 is meant to have origin in the past and destination in the future (qualifies)
        //route2 is meant to have origin in the past and destination in the past (not qualified)
        //route3 is meant to have origin and destination in the future (not qualified)
        LocationService mockLocationService = new MockLocationServiceImpl(mockPersistenceManager);
        RouteService routeService = new RouteServiceImpl(mockPersistenceManager, mockLocationService);
        DeliveryRoute route1 = routeService.createDeliveryRoute(validTime, validCities);
        DeliveryRoute route2 = routeService.createDeliveryRoute(validTime, validCities);
        DeliveryRoute route3 = routeService.createDeliveryRoute(validTime, validCities);

        Location originObj1 = mockLocationService.getLocationById(route1.getOrigin());
        Location originObj2 = mockLocationService.getLocationById(route2.getOrigin());
        Location destinationObj2 = mockLocationService.getLocationById(route2.getDestination());

        originObj1.setDepartureTime(LocalDateTime.now().minusHours(1));
        originObj2.setDepartureTime(LocalDateTime.now().minusHours(2));
        ((MockLocationImpl) destinationObj2).setArrivalTime(LocalDateTime.now().minusHours(1));
        Assertions.assertEquals(1, routeService.getRoutesInProgress().size());
    }

    @Test
    public void getRoutesInProgress_Should_ReturnEmpty_When_NoActiveRoutes(){
        DeliveryRoute route1 = routeService.createDeliveryRoute(validTime, validCities);
        DeliveryRoute route2 = routeService.createDeliveryRoute(validTime, validCities);
        Assertions.assertTrue(routeService.getRoutesInProgress().isEmpty());
    }

    @Test
    public void findRoutesServicingStartAndEnd_Should_ReturnMatchingRoutes(){
        DeliveryRoute route1 = routeService.createDeliveryRoute(validTime, List.of(City.SYD, City.MEL, City.ADL));
        DeliveryRoute route2 = routeService.createDeliveryRoute(validTime, List.of(City.ADL, City.MEL, City.SYD));
        List <DeliveryRoute> result = routeService.findRoutesServicingStartAndEnd(City.SYD, City.MEL);
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertEquals(route1.getId(),
                        routeService.findRoutesServicingStartAndEnd(City.SYD, City.MEL).get(0).getId())
        );
    }

    @Test
    public void findRoutesServicingStartAndEnd_Should_Throw_When_OriginEqualsDestination(){
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> routeService.findRoutesServicingStartAndEnd(City.SYD, City.SYD));
    }

    @Test
    public void findRoutesServicingStartAndEnd_Should_ReturnEmpty_When_NoMatch(){
        Assertions.assertEquals(0, routeService.findRoutesServicingStartAndEnd(City.SYD, City.MEL).size());
    }

    @Test
    public void findRoutesServicingStartAndEnd_Should_ReturnEmpty_When_MatchDepartedOrigin(){
        List<City> cities = List.of(City.SYD, City.MEL, City.BRI);
        LocationService mockLocationService = new MockLocationServiceImpl(mockPersistenceManager);
        RouteService routeService = new RouteServiceImpl(mockPersistenceManager, mockLocationService);
        DeliveryRoute route1 = routeService.createDeliveryRoute(validTime, cities);
        int originId = route1.getOrigin();
        Location originObj = mockLocationService.getLocationById(originId);
        originObj.setDepartureTime(LocalDateTime.now().minusHours(1));

        Assertions.assertEquals(0, routeService.findRoutesServicingStartAndEnd(City.SYD, City.MEL).size());
    }

    @Test
    public void assignVehicle_Should_AssignTruckToRoute(){
        DeliveryRoute route1 = routeService.createDeliveryRoute(validTime, validCities);
        int mockVehicleId = 5;
        routeService.assignVehicle(mockVehicleId, route1.getId());
        Assertions.assertEquals(mockVehicleId, route1.getAssignedVehicleId());
    }

    @Test
    public void assignPackage_Should_AddPackageToRoute(){
        DeliveryRoute route1 = routeService.createDeliveryRoute(validTime, validCities);
        int mockPackageId = 5;
        routeService.assignPackage(route1.getId(), mockPackageId);
        Assertions.assertAll(
                () -> Assertions.assertEquals(mockPackageId, route1.getAssignedPackages().get(0)),
                () -> Assertions.assertEquals(1, route1.getAssignedPackages().size())
            );
    }

    @Test
    public void getMatchingLocations_Should_ReturnCorrectSubset(){
        List<City> cities = List.of(City.SYD, City.MEL, City.ADL, City.BRI);
        DeliveryRoute route1 = routeService.createDeliveryRoute(validTime, cities);
        Assertions.assertEquals(
                List.of(2, 3),
                routeService.getMatchingLocations(route1.getId(), cities.get(1), cities.get(2)).stream()
                        .map(Identifiable::getId).toList()
        );

    }

    @Test
    public void getMatchingLocations_ShouldThrow_WhenRouteDoesNotCoverCities(){
        List<City> cities = List.of(City.SYD, City.MEL, City.BRI);
        DeliveryRoute route1 = routeService.createDeliveryRoute(validTime, cities);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> routeService.getMatchingLocations(route1.getId(), cities.get(1), City.ADL)
        );
    }
}
