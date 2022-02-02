package com.eventstore.training.scheduling.domain.readmodel.availableslots;

import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;

public record AvailableSlot(
    @NonNull String slotId,
    @NonNull LocalDateTime startTime,
    @NonNull Duration duration
) { }
