package com.company.oop.logistics.utils.misc;

import com.company.oop.logistics.models.contracts.Identifiable;

import java.util.List;

public class IdUtils {
    public static int getNextId(List<? extends Identifiable> list){
        return list.stream().mapToInt(Identifiable::getId).max().orElse(0) + 1;
    }
}
