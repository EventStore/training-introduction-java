package com.eventstore.training.scheduling.domain.writemodel.command;

import com.eventstore.training.scheduling.eventsourcing.Command;

public record Book(
) implements Command { }
