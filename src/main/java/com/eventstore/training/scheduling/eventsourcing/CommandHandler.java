package com.eventstore.training.scheduling.eventsourcing;

import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final AggregateStore repository;

  public CommandHandler(AggregateStore repository) {
    this.repository = repository;
  }

  public <T extends Aggregate<T, S>, S extends State<S>> Try<T> handle(
      Class<T> clazz, String aggregateId, Command command) {
    return repository
        .reconsititute(clazz, aggregateId)
        .flatMap(
            aggregate ->
                Try.of(
                    () -> {
                      logger.info("Handling command: {}", command);
                      aggregate.handle(command);
                      return aggregate;
                    }))
        .flatMap(repository::commitChanges);
  }
}
