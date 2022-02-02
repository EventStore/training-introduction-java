package com.eventstore.training.scheduling.domain.writemodel.command;

import com.eventstore.training.scheduling.eventsourcing.Command;
import lombok.NonNull;

public record Book(
    @NonNull String id,
    @NonNull String patientId
) implements Command { }
