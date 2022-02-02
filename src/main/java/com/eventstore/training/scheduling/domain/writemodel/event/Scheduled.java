package com.eventstore.training.scheduling.domain.writemodel.event;

import com.eventstore.training.scheduling.eventsourcing.Event;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;

public record Scheduled(
    @NonNull String slotId,
    @NonNull LocalDateTime startTime,
    @NonNull Duration duration
) implements Event { }
