package com.company.oop.logistics.models;

public abstract class Vehicle {
    private int capacity;
    private int maxRange;
    private int id;


    public Vehicle(int capacity, int maxRange, int id) {
        this.capacity = capacity;
        this.maxRange = maxRange;
        this.id = id;
    }

    public Vehicle() {

    }
}
