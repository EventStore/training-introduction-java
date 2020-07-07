package com.eventstore.training.scheduling.eventsourcing;

import com.eventstore.training.scheduling.eventsourcing.Event;
import com.eventstore.training.scheduling.eventsourcing.EventHandler;
import io.vavr.collection.List;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventHandlerTest {
  protected EventHandler handler;

  protected String randomString() {
    return UUID.randomUUID().toString().replace("-", "").substring(8);
  }

  protected void given(Event... events) {
    List.of(events).forEach(event -> handler.handle(event, null));
  }

  protected void then(Object expected, Object actual) {
    assertEquals(expected, actual);
  }
}
