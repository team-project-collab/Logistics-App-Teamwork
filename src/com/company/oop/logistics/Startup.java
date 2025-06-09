package com.company.oop.logistics;

import com.company.oop.logistics.core.EngineImpl;

public class Startup {

    public static void main(String[] args) {
        EngineImpl engine = new EngineImpl();
        engine.start();
    }

}
