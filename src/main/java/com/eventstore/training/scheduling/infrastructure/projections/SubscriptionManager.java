package com.eventstore.training.scheduling.infrastructure.projections;

import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.ResolvedEvent;
import com.eventstore.dbclient.SubscriptionListener;
import com.eventstore.training.scheduling.infrastructure.eventstore.EsEventSerde;
import io.vavr.collection.List;
import lombok.SneakyThrows;
import lombok.val;

public class SubscriptionManager {
    private final EventStoreDBClient client;
    private final List<Subscription> subscriptions;
    private final EsEventSerde serde = new EsEventSerde();

    public SubscriptionManager(EventStoreDBClient client, List<Subscription> subscriptions) {
        this.client = client;
        this.subscriptions = subscriptions;
    }

    @SneakyThrows
    public void start() {
        client.subscribeToAll(new Listener()).get();
    }

    private void eventAppeared(ResolvedEvent event) {
        if (event.getEvent().getEventType().startsWith("$")) {
            return;
        }

        val deserialized = serde.deserialise(event);

        subscriptions.forEach(s -> s.project(deserialized));
    }

    private class Listener extends SubscriptionListener {
        @Override
        public void onEvent(com.eventstore.dbclient.Subscription subscription, ResolvedEvent event) {
            eventAppeared(event);
        }
    }
}
