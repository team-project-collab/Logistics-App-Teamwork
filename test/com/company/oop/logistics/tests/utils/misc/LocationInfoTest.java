package com.company.oop.logistics.tests.utils.misc;

import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.modelservices.contracts.LocationService;
import com.company.oop.logistics.utils.misc.LocationInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testingUtils.MockLocationServiceImpl;
import testingUtils.MockPersistenceManagerImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocationInfoTest {
    private LocationService locationService;
    private final LocalDateTime now = LocalDateTime.now();
    private LocationInfo locationInfo;
    private List<Integer> locationIds;
    private Location loc1;
    private Location loc2;
    private Location loc3;
    private Location loc4;
    private Location loc5;
    private Location loc6;

    @BeforeEach
    public void setUp(){
        PersistenceManager persistenceManager = new MockPersistenceManagerImpl();
        locationService = new MockLocationServiceImpl(persistenceManager);
        loc1 = locationService.createLocation(City.SYD, now.minusHours(10), null);
        loc2 = locationService.createLocation(City.SYD, null, now.minusHours(8));
        loc3 = locationService.createLocation(City.ADL, now.minusHours(6), now.minusHours(4));
        loc4 = locationService.createLocation(City.BRI, now.plusHours(2), now.plusHours(3));
        loc5 = locationService.createLocation(City.PER, now.plusHours(4), now.plusHours(5));
        loc6 = locationService.createLocation(City.ASP, now.plusHours(7), null);
        locationIds = locationService.getAllLocations().stream().map(Location::getId).toList();
    }

    @Test
    public void constructor_Should_InitializeLocations() {
        locationInfo = new LocationInfo(
                locationService,
                locationIds,
                now
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        loc2.getDepartureTime(),
                        locationInfo.getPreviousLocation().getDepartureTime()
                ),
                () -> Assertions.assertEquals(
                        loc3.getDepartureTime(),
                        locationInfo.getCurrentLocation().getDepartureTime()
                ),
                () -> Assertions.assertEquals(
                        loc4.getDepartureTime(),
                        locationInfo.getNextLocation().getDepartureTime()
                ),
                () -> Assertions.assertEquals(
                        loc5.getDepartureTime(),
                        locationInfo.getLocationAfterNext().getDepartureTime()
                ),
                () -> Assertions.assertEquals(
                        loc6.getArrivalTime(),
                        locationInfo.getLastLocation().getArrivalTime()
                )
        );
    }

    //getTruckStatus tests
    @Test
    public void getTruckStatus_Should_GetFreeStatus_When_OnlyOneLocation(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds.subList(0,1),
                now
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_VEHICLE_FREE, loc1.getName()),
                locationInfo.getTruckStatus()
        );
    }

    @Test
    public void getTruckStatus_Should_GetNotStartedStatus_When_True(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds,
                now.minusHours(23)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_VEHICLE_AWAITING_DEPARTURE,
                        loc2.getName(),
                        loc3.getName(),
                        loc2.getDepartureTime().format(LocationInfo.formatter)),
                locationInfo.getTruckStatus()
        );
    }

    @Test
    public void getTruckStatus_Should_GetInProgressMovingStatus_When_True(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds,
                now.minusHours(3)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_VEHICLE_ONROUTE_TRAVELING,
                        loc3.getName(),
                        loc4.getName(),
                        loc4.getArrivalTime().format(LocationInfo.formatter)),
                locationInfo.getTruckStatus()
        );
    }

    @Test
    public void getTruckStatus_Should_GetInProgressStationaryStatus_When_True(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds,
                now.minusHours(5)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_VEHICLE_ONROUTE_STATIONED,
                        loc3.getName(),
                        loc4.getName(),
                        loc3.getDepartureTime().format(LocationInfo.formatter)),
                locationInfo.getTruckStatus()
        );
    }

    @Test
    public void getTruckStatus_Should_GetFinishedStatus_When_True(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds,
                now.plusHours(23)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_VEHICLE_FREE, loc6.getName()),
                locationInfo.getTruckStatus()
        );
    }

    @Test
    public void getTruckStatus_Should_GetInProgressMovingStatus_When_LeftFirstStop(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds,
                now.minusHours(7)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_VEHICLE_ONROUTE_TRAVELING,
                        loc2.getName(),
                        loc3.getName(),
                        loc3.getArrivalTime().format(LocationInfo.formatter)),
                locationInfo.getTruckStatus()
        );
    }

    //getRouteStatus tests
    @Test
    public void getRouteStatus_Should_GetMessageNotStarted(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds.subList(1,5),
                now.minusHours(23)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_ROUTE_NOT_STARTED,
                        loc2.getName(),
                        loc2.getDepartureTime().format(LocationInfo.formatter),
                        loc3.getName()),
                locationInfo.getRouteStatus()
        );
    }

    @Test
    public void getRouteStatus_Should_GetInProgressMovingStatus_When_LeftFirstLocation(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds.subList(1,5),
                now.minusHours(7)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_ROUTE_ONROUTE_TRAVELING,
                        loc2.getName(),
                        loc3.getName(),
                        loc3.getArrivalTime().format(LocationInfo.formatter)),
                locationInfo.getRouteStatus()
        );
    }

    @Test
    public void getRouteStatus_Should_GetInProgressMovingStatus_When_LeftSecondLocation(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds.subList(1,5),
                now.minusHours(3)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_ROUTE_ONROUTE_TRAVELING,
                        loc3.getName(),
                        loc4.getName(),
                        loc4.getArrivalTime().format(LocationInfo.formatter)),
                locationInfo.getRouteStatus()
        );
    }

    @Test
    public void getRouteStatus_Should_GetInProgressStationaryStatus(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds.subList(1,5),
                now.minusHours(5)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_ROUTE_ONROUTE_STATIONED,
                        loc3.getName(),
                        loc4.getName(),
                        loc3.getDepartureTime().format(LocationInfo.formatter)),
                locationInfo.getRouteStatus()
        );
    }

    @Test
    public void getRouteStatus_Should_GetFinishedStatus(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds.subList(1,6),
                now.plusHours(23)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_ROUTE_FINISHED, loc6.getName()),
                locationInfo.getRouteStatus()
        );
    }

    //getPackageStatus tests
    @Test
    public void getPackageStatus_Should_GetNotAssignedStatus_When_OneLocation(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds.subList(0,1),
                now.minusHours(23)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_PACKAGE_NOT_ASSIGNED, loc1.getName()),
                locationInfo.getPackageStatus()
        );
    }

    @Test
    public void getPackageStatus_Should_GetMessageNotStarted_When_CurrentLocationIsSecond(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds,
                now.minusHours(9)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_PACKAGE_NOT_STARTED,
                        loc2.getName(),
                        loc2.getDepartureTime().format(LocationInfo.formatter),
                        loc6.getName(),
                        loc6.getArrivalTime().format(LocationInfo.formatter)),
                locationInfo.getPackageStatus()
        );
    }

    @Test void getPackageStatus_Should_GetOnRouteTravelling(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds,
                now.minusHours(7)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_PACKAGE_TRAVELING,
                        loc6.getName(),
                        loc6.getArrivalTime().format(LocationInfo.formatter)),
                locationInfo.getPackageStatus()
        );
    }

    @Test void getPackageStatus_Should_GetOnRouteStationary(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds,
                now.minusHours(5)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_PACKAGE_STATIONARY,
                        loc3.getName(),
                        loc3.getDepartureTime().format(LocationInfo.formatter),
                        loc6.getName(),
                        loc6.getArrivalTime()),
                locationInfo.getPackageStatus()
        );
    }

    @Test void getPackageStatus_Should_GetOnRouteTravelling2(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds,
                now.minusHours(3)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_PACKAGE_TRAVELING,
                        loc6.getName(),
                        loc6.getArrivalTime().format(LocationInfo.formatter)),
                locationInfo.getPackageStatus()
        );
    }

    @Test void getPackageStatus_Should_getDeliveredStatus(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds,
                now.plusHours(23)
        );
        Assertions.assertEquals(
                String.format(LocationInfo.MESSAGE_PACKAGE_DELIVERED, loc6.getName()),
                locationInfo.getPackageStatus()
        );
    }

    @Test void generateLocations_Should_GenerateEmptyCurrent_When_NoLocations(){
        locationInfo = new LocationInfo(
                locationService,
                locationIds.subList(0,0),
                now.plusHours(23)
        );
        Assertions.assertNull(locationInfo.getCurrentLocation());
    }

    @Test void generateLocations_Should_GenerateEmptyCurrent_When_EmptyLocations(){
        locationInfo = new LocationInfo(
                locationService,
                null,
                now.plusHours(23)
        );
        Assertions.assertNull(locationInfo.getCurrentLocation());
    }
}
