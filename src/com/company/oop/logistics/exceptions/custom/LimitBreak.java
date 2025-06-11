package com.company.oop.logistics.exceptions.custom;

public class LimitBreak extends RuntimeException{
    public LimitBreak(String message){
        super(message);
    }
}
