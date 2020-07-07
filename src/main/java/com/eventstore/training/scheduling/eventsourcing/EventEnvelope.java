package com.eventstore.training.scheduling.eventsourcing;

import lombok.Data;
import lombok.NonNull;

@Data
public class EventEnvelope {
  public final @NonNull Event event;
  public final @NonNull EventMetadata metadata;
}
