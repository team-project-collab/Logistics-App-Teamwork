package com.company.oop.logistics.core;

import com.company.oop.logistics.commands.CommandType;
import com.company.oop.logistics.commands.TestCommand;
import com.company.oop.logistics.commands.TestCommandWithParams;
import com.company.oop.logistics.commands.contracts.Command;
import com.company.oop.logistics.core.contracts.CommandFactory;
import com.company.oop.logistics.core.contracts.ObjectRepository;

public class CommandFactoryImpl implements CommandFactory {
    @Override
    public Command createCommandFromCommandName(String commandTypeValue, ObjectRepository objectRepository) {
        CommandType commandType;
        try {
            commandType = CommandType.valueOf(commandTypeValue.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(String.format("Command %s is not supported.", commandTypeValue));
        }

        switch (commandType) {
            case TESTCOMMAND:
                return new TestCommand();
            case TESTCOMMANDWITHPARAMS:
                return new TestCommandWithParams();
        }
        return null;
    }
}
