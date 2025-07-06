package com.company.oop.logistics.models.enums;

public enum TruckName {
    SCANIA,
    MAN,
    ACTROS;

    @Override
    public String toString() {
        return switch (this){
            case SCANIA -> "Scania";
            case MAN -> "Man";
            case ACTROS ->  "Actros";
        };
    }
}
