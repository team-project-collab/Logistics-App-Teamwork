package com.company.oop.logistics.commands;

import com.company.oop.logistics.commands.contracts.Command;

import java.util.List;

public class TestCommand implements Command {

    @Override
    public String execute(List<String> parameters) {
        return "Test command executed";
    }
}
