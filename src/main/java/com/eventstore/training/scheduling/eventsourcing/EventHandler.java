package com.eventstore.training.scheduling.eventsourcing;

public interface EventHandler {
  void handle(Event event, EventMetadata eventMetadata);
}
