package com.company.oop.logistics.models;

import com.company.oop.logistics.commands.CommandType;
import com.company.oop.logistics.exceptions.custom.LimitBreak;
import com.company.oop.logistics.models.contracts.Truck;
import com.company.oop.logistics.models.enums.TruckName;
import com.company.oop.logistics.utils.parcing.ParsingHelpers;

public class TruckImpl implements Truck {
    private int capacity;
    private int maxRange;
    private TruckName truckName;

    public TruckName getTruckName() {
        return truckName;
    }

    public void setTruckName(TruckName truckName) {
        this.truckName = truckName;
    }

    public void setId(int id) {
        this.id = id;
    }

    private static int idScania = 1001;
    private static int idMan = 1011;
    private static int idActros = 1026;
    private int id;

    public TruckImpl(String name) throws LimitBreak {
         setTruckName(ParsingHelpers.tryParseEnum(name, TruckName.class, String.format("Wrong truck type %s", name)));

         if(getTruckName() == TruckName.SCANIA){
            setCapacity(42000);
            setMaxRange(8000);
            if(idScania < 1011){
                setId(idScania);
                idScania++;
            }else{
                throw new LimitBreak(String.format("Company cannot provide additional %s trucks",name.toUpperCase()));
            }
         }
         if(getTruckName() == TruckName.MAN){
             setCapacity(37000);
             setMaxRange(10000);
             if(idMan < 1026){
                 setId(idMan);
                 idMan++;
             }else{
                 throw new LimitBreak(String.format("Company cannot provide additional %s trucks",name.toUpperCase()));
             }
         }
        if(getTruckName() == TruckName.ACTROS){
            setCapacity(26000);
            setMaxRange(13000);
            if(idActros < 1041){
                setId(idActros);
                idActros++;
            }else{
                throw new LimitBreak(String.format("Company cannot provide additional %s trucks",name.toUpperCase()));
            }
        }

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
}
