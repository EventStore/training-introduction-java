package com.eventstore.training.scheduling.domain.writemodel.event;

import com.eventstore.training.scheduling.eventsourcing.Event;
import lombok.Data;
import lombok.NonNull;

@Data
public class Cancelled implements Event {
  private final @NonNull String slotId;
  private final @NonNull String reason;
}
