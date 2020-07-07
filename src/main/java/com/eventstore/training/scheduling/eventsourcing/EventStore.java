package com.eventstore.training.scheduling.eventsourcing;

import io.vavr.collection.List;

public interface EventStore {
  List<EventEnvelope> readAll(Long fromPosition);

  List<EventEnvelope> readFromStream(String streamId, Long fromPosition);

  void createNewStream(String streamId, List<Event> events);

  void appendToStream(
      String streamId, Long expectedVersion, List<Event> events);
}
