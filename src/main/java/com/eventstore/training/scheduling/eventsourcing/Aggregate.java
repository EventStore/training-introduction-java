package com.eventstore.training.scheduling.eventsourcing;

import io.vavr.collection.List;

public abstract class Aggregate<A extends Aggregate<A, S>, S extends State<S>> {
  private final String id;
  protected S state;
  private Long version = -1L;
  private List<Event> changes = List.empty();

  public Aggregate(String id, S state) {
    this.id = id;
    this.state = state;
  }

  public String getId() {
    return id;
  }

  public Long getVersion() {
    return version;
  }

  public boolean isNew() {
    return -1L == version;
  }

  public abstract List<? extends Event> calculateChanges(Command command) throws Error;

  public void handle(Command command) throws Error {
    changes = changes.appendAll(calculateChanges(command));
  }

  public void reconstitute(List<Event> events) {
    state.apply(events);
    version = version + events.length();
  }

  public void markAsCommitted() {
    version = version + changes.length();
    changes = List.empty();
  }

  public List<Event> getChanges() {
    return changes;
  }
}
