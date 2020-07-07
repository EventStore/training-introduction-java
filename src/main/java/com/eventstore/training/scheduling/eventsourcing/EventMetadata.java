package com.eventstore.training.scheduling.eventsourcing;

import lombok.Data;
import lombok.NonNull;

@Data
public class EventMetadata {
  private final @NonNull long position;
}
