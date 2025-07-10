package com.company.oop.logistics.utils.constants;

import com.company.oop.logistics.models.enums.City;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CityDistance {
    private static final HashMap<City, Map<City, Integer>> distances = new HashMap<>();

    static {
        add(City.SYD, City.MEL, 877);
        add(City.MEL, City.SYD, 877);

        add(City.SYD, City.ADL, 1376);
        add(City.ADL, City.SYD, 1376);

        add(City.SYD, City.ASP, 2762);
        add(City.ASP, City.SYD, 2762);

        add(City.SYD, City.BRI, 909);
        add(City.BRI, City.SYD, 909);

        add(City.SYD, City.DAR, 3935);
        add(City.DAR, City.SYD, 3935);

        add(City.SYD, City.PER, 4016);
        add(City.PER, City.SYD, 4016);

        add(City.MEL, City.ADL, 725);
        add(City.ADL, City.MEL, 725);

        add(City.MEL, City.ASP, 2255);
        add(City.ASP, City.MEL, 2255);

        add(City.MEL, City.BRI, 1765);
        add(City.BRI, City.MEL, 1765);

        add(City.MEL, City.DAR, 3752);
        add(City.DAR, City.MEL, 3752);

        add(City.MEL, City.PER, 3509);
        add(City.PER, City.MEL, 3509);

        add(City.ADL, City.ASP, 1530);
        add(City.ASP, City.ADL, 1530);

        add(City.ADL, City.BRI, 1927);
        add(City.BRI, City.ADL, 1927);

        add(City.ADL, City.DAR, 3027);
        add(City.DAR, City.ADL, 3027);

        add(City.ADL, City.PER, 2785);
        add(City.PER, City.ADL, 2785);

        add(City.ASP, City.BRI, 2993);
        add(City.BRI, City.ASP, 2993);

        add(City.ASP, City.DAR, 1497);
        add(City.DAR, City.ASP, 1497);

        add(City.ASP, City.PER, 2481);
        add(City.PER, City.ASP, 2481);

        add(City.BRI, City.DAR, 3426);
        add(City.DAR, City.BRI, 3426);

        add(City.BRI, City.PER, 4311);
        add(City.PER, City.BRI, 4311);

        add(City.DAR, City.PER, 4025);
        add(City.PER, City.DAR, 4025);
    }

    public static void add(City city1, City city2, int distance){
        Map<City, Integer> innerMap = distances.get(city1);
        if (innerMap == null){
            innerMap = new HashMap<>();
            distances.put(city1, innerMap);
        }
        distances.get(city1).put(city2,distance);
    }

    public static int getDistance(City city1, City city2){
        return distances.get(city1).get(city2);
    }

    public static int getDistance(List<City> cities){
        int total = 0;
        for (int i = 0; i < cities.size() - 1; i++) {
            total += getDistance(cities.get(i), cities.get(i + 1));
        }
        return total;
    }

    public static long getTravelTimeSeconds(City city1, City city2, int vehicleSpeed){
        return (long) Math.ceil((double) distances.get(city1).get(city2) / vehicleSpeed * 60) * 60;
    }
}
