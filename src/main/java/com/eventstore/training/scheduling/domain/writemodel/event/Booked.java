package com.eventstore.training.scheduling.domain.writemodel.event;

import com.eventstore.training.scheduling.eventsourcing.Event;
import lombok.NonNull;

public record Booked(
    @NonNull String slotId,
    @NonNull String patientId
) implements Event { }
