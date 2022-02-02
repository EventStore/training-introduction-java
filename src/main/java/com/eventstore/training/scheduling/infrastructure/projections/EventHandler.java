package com.eventstore.training.scheduling.infrastructure.projections;

import java.util.function.Consumer;

public record EventHandler(
    Class type,
    Consumer<Object> handler
) { }
