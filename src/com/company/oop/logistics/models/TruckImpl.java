package com.company.oop.logistics.models;

import com.company.oop.logistics.exceptions.custom.LimitBreak;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.TruckName;
import com.company.oop.logistics.utils.parsing.ParsingHelpers;

import java.util.ArrayList;
import java.util.List;

public class TruckImpl extends Vehicle implements Truck {
    private int capacity;
    private int maxRange;
    private int id;
    private TruckName truckName;
    private final List<Integer> locationIds = new ArrayList<>();

    private static int idScania;
    private static int idMan;
    private static int idActros;


    public TruckImpl(String name) throws LimitBreak {
        super();
        setTruckName(ParsingHelpers.tryParseEnum(name, TruckName.class, String.format("Wrong truck type %s", name)));
        setUpTruck();
    }

    public static void setIds(int scaniaStartId, int manStartId, int actrosStartId){
        idScania = scaniaStartId;
        idMan = manStartId;
        idActros = actrosStartId;
    }

    public  void setUpTruck(){
        if(getTruckName() == TruckName.SCANIA){
            setCapacity(42000);
            setMaxRange(8000);
            if(idScania < 1011){
                setId(idScania);
                idScania++;
            }else{
                throw new LimitBreak(String.format("Company cannot provide additional %s trucks",getTruckName()));
            }
        }
        if(getTruckName() == TruckName.MAN){
            setCapacity(37000);
            setMaxRange(10000);
            if(idMan < 1026){
                setId(idMan);
                idMan++;
            }else{
                throw new LimitBreak(String.format("Company cannot provide additional %s trucks",getTruckName()));
            }
        }
        if(getTruckName() == TruckName.ACTROS){
            setCapacity(26000);
            setMaxRange(13000);
            if(idActros < 1041){
                setId(idActros);
                idActros++;
            }else{
                throw new LimitBreak(String.format("Company cannot provide additional %s trucks",getTruckName()));
            }
        }
    }
    public static void resetTruckLimit(){
        setIds(0,0,0);
    }

    public int getCapacity() {
        return capacity;
    }

    private void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getMaxRange() {
        return maxRange;
    }

    private void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }

    @Override
    public int getId() {
        return this.id;
    }
    public TruckName getTruckName() {
        return truckName;
    }

    public void setTruckName(TruckName truckName) {
        this.truckName = truckName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getLocationIds() {
        return new ArrayList<>(locationIds);
    }

    public void addLocationIds(List<Integer> locationIds) {
        this.locationIds.addAll(locationIds);
    }

    @Override
    public String toString() {
        return String.format("""
                id: %d
                Brand: %d
                Capacity: %d
                Max range: %d""");
    }
}
