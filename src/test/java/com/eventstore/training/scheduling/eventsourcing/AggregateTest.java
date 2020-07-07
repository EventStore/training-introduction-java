package com.eventstore.training.scheduling.eventsourcing;

import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AggregateTest<A extends Aggregate<A, ?>> {
  protected A aggregate;
  protected Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
  private Try<List<? extends Event>> result;

  protected String randomString() {
    return UUID.randomUUID().toString();
  }

  @BeforeEach
  void beforeEach() {
    aggregate = newInstance();
    result = null;
  }

  protected void given(Event... events) {
    aggregate.reconstitute(List.of(events));
  }

  protected void when(Command command) {
    result =
        Try.of(
            () -> {
              aggregate.handle(command);
              return aggregate.getChanges();
            });
  }

  protected void then(Event event) {
    List<? extends Event> changes = result.get();
    assertEquals(changes, List.of(event));

    val version = aggregate.getVersion();
    aggregate.markAsCommitted();
    assertEquals(aggregate.getVersion(), version + changes.length());
  }

  protected void then(Error error) {
    assertEquals(error, result.failed().get());
  }

  protected abstract A newInstance();
}
