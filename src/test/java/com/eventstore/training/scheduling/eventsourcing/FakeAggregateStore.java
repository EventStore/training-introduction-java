package com.eventstore.training.scheduling.eventsourcing;

public record FakeAggregateStore(AggregateRoot aggregate) implements AggregateStore {
    @Override
    public <T extends AggregateRoot> void save(T aggregate) { }

    @Override
    public <T extends AggregateRoot> T load(Class<T> clazz, String aggregateId) {
        return (T) aggregate;
    }
}
