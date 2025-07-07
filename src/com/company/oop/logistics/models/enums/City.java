package com.company.oop.logistics.models.enums;

public enum City {
    SYD,
    MEL,
    ADL,
    ASP,
    BRI,
    DAR,
    PER;

    public String toString() {
        return switch (this) {
            case SYD -> "Sydney";
            case MEL -> "Melbourne";
            case ADL -> "Adelaide";
            case ASP -> "Alice Springs";
            case BRI -> "Brisbane";
            case DAR -> "Darwin";
            case PER -> "Perth";
        };
    }
}



