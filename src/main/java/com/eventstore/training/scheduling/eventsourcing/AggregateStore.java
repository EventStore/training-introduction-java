package com.eventstore.training.scheduling.eventsourcing;

import com.eventstore.training.scheduling.domain.slot.writemodel.SlotAggregate;
import io.vavr.control.Try;
import lombok.val;

import java.time.Clock;

public class AggregateStore {
  private final EventStore eventStore;
  private final Clock clock;

  public AggregateStore(EventStore eventStore, Clock clock) {
    this.eventStore = eventStore;
    this.clock = clock;
  }

  public <T extends Aggregate<T, S>, S extends State<S>> Try<T> reconsititute(
      Class<T> clazz, String id) {
    return Try.of(
        () -> {
          val aggregate = createInstance(clazz, id);
          val events = eventStore.readFromStream(getStreamId(clazz, aggregate.getId()), 0L);
          aggregate.reconstitute(events.map(EventEnvelope::getEvent));
          return aggregate;
        });
  }

  private <T extends Aggregate<T, S>, S extends State<S>> T createInstance(
      Class<T> clazz, String id) {
    if (clazz.isAssignableFrom(SlotAggregate.class))
      return (T) new SlotAggregate(id, clock);
    else
      throw new RuntimeException("Unable to instantiate an aggregate of class " + clazz.toString());
  }

  private String getStreamId(Class<?> clazz, String id) {
    if (clazz.isAssignableFrom(SlotAggregate.class)) return "slot-" + id;
    else throw new RuntimeException("Unable to get stream id if for class " + clazz.toString());
  }

  public <T extends Aggregate<T, S>, S extends State<S>> Try<T> commitChanges(T aggregate) {
    return Try.of(
        () -> {
          if (aggregate.isNew()) {
            eventStore.createNewStream(
                getStreamId(aggregate.getClass(), aggregate.getId()),
                aggregate.getChanges());
          } else {
            eventStore.appendToStream(
                getStreamId(aggregate.getClass(), aggregate.getId()),
                aggregate.getVersion(),
                aggregate.getChanges());
          }
          aggregate.markAsCommitted();
          return aggregate;
        });
  }
}
