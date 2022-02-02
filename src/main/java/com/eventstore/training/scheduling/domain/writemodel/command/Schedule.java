package com.eventstore.training.scheduling.domain.writemodel.command;

import com.eventstore.training.scheduling.eventsourcing.Command;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;

public record Schedule(
    @NonNull String id,
    @NonNull LocalDateTime startTime,
    @NonNull Duration duration
) implements Command { }
