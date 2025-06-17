package com.company.oop.logistics.core.contracts;

import com.company.oop.logistics.commands.contracts.Command;

public interface CommandFactory{
    Command createCommandFromCommandName(String commandType);
}
