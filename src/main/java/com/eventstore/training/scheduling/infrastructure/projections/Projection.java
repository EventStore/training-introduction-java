package com.eventstore.training.scheduling.infrastructure.projections;

import io.vavr.collection.List;
import lombok.Getter;

import java.util.function.Consumer;

public class Projection {
  @Getter
  private List<EventHandler> handlers = List.empty();

  protected <T> void when(Class<T> clazz, Consumer<T> when) {
    handlers = handlers.append(new EventHandler(clazz, (Consumer<Object>) when));
  }

  public void handle(Object event) {
    handlers.forEach(handler -> {
      if (handler.type() == event.getClass()) {
        handler.handler().accept(event);
      }
    });
  }

  public boolean canHandle(Object event) {
    return handlers.exists(handler -> handler.type() == event.getClass());
  }
}
