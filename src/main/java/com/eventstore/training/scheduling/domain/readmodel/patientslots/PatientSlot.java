package com.eventstore.training.scheduling.domain.readmodel.patientslots;

import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;

public record PatientSlot(
    @NonNull String slotId,
    @NonNull LocalDateTime startTime,
    @NonNull Duration duration,
    @NonNull String status
) { }
