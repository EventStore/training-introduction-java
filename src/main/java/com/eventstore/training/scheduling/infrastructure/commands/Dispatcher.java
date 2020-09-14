package com.eventstore.training.scheduling.infrastructure.commands;

import lombok.var;

public class Dispatcher {
    private final CommandHandlerMap map;

    public Dispatcher(CommandHandlerMap map) {
        this.map = map;
    }

    public void dispatch(Object command) {
        var handler = map.get(command);

        if (handler.isDefined()) {
            handler.get().accept(command);
        } else {
            throw new HandlerNotFound(command);
        }
    }
}
