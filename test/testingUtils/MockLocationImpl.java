package testingUtils;

import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import com.company.oop.logistics.models.enums.LocationType;

import java.time.LocalDateTime;

public class MockLocationImpl implements Location {

    private int id;
    private City name;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private LocationType type;

    public MockLocationImpl(int id, City name, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        setId(id);
        setName(name);
        setArrivalTime(arrivalTime);
        setDepartureTime(departureTime);
        setType();
    }

    private void setId(int id){
        this.id = id;
    }

    private void setName(City name) {
        this.name = name;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        if (arrivalTime != null) {
            this.arrivalTime = arrivalTime;
        }
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        if (departureTime != null) {
            this.departureTime = departureTime;
        }
    }

    @Override
    public City getName() {
        return name;
    }

    @Override
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public LocationType getType() {
        return type;
    }

    private void setType(){
        if (arrivalTime == null){
            type = LocationType.START;
        } else if (departureTime == null) {
            type = LocationType.END;
        }else {
            type = LocationType.INTERMEDIATE;
        }
    }
}
