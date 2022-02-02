package com.eventstore.training.scheduling.domain.writemodel.command;
import com.eventstore.training.scheduling.eventsourcing.Command;
import lombok.NonNull;

import java.time.LocalDateTime;

public record Cancel(
    @NonNull String id,
    @NonNull String reason,
    @NonNull LocalDateTime cancellationTime
) implements Command { }
