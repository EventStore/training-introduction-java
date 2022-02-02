package com.eventstore.training.scheduling.domain.writemodel.event;

import com.eventstore.training.scheduling.eventsourcing.Event;
import lombok.NonNull;

public record Cancelled(
    @NonNull String slotId,
    @NonNull String reason
) implements Event { }
