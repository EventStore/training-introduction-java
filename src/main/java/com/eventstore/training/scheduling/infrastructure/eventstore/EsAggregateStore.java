package com.eventstore.training.scheduling.infrastructure.eventstore;

import com.eventstore.training.scheduling.eventsourcing.AggregateRoot;
import com.eventstore.training.scheduling.eventsourcing.AggregateStore;
import com.eventstore.training.scheduling.eventsourcing.EventStore;
import lombok.SneakyThrows;
import lombok.val;

public record EsAggregateStore(EventStore esEventStore) implements AggregateStore {

    @Override
    public <T extends AggregateRoot> void save(T aggregate) {
        val streamName = getStreamName(aggregate);
        val changes = aggregate.getChanges();

        esEventStore.appendEvents(streamName, aggregate.getVersion(), changes);
        aggregate.clearChanges();
    }

    private <T extends AggregateRoot> String getStreamName(T aggregate) {
        return getStreamName(aggregate.getClass(), aggregate.getId());
    }

    private <T extends AggregateRoot> String getStreamName(Class<T> clazz, String aggregateId) {
        return "%s-%s".formatted(clazz.getSimpleName(), aggregateId);
    }

    @SneakyThrows
    @Override
    public <T extends AggregateRoot> T load(Class<T> clazz, String aggregateId) {
        val streamName = getStreamName(clazz, aggregateId);
        val aggregate = clazz.getDeclaredConstructor().newInstance();
        aggregate.setId(aggregateId);

        val events = esEventStore.loadEvents(streamName);

        aggregate.load(events);
        aggregate.clearChanges();

        return aggregate;
    }
}
