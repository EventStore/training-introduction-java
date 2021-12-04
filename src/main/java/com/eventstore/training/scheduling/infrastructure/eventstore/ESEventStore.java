package com.eventstore.training.scheduling.infrastructure.eventstore;

import com.eventstore.dbclient.*;
import com.eventstore.training.scheduling.eventsourcing.EventStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ESEventStore implements EventStore {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final EventStoreDBClient client;
    private final EsEventSerde serde = new EsEventSerde();

    public ESEventStore(EventStoreDBClient client) {
        this.client = client;
    }

    @SneakyThrows
    @Override
    public void appendEvents(String streamName, Long version, List<Object> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        java.util.Iterator<EventData> preparedEvents = events.map(serde::serialise).iterator();

        ExpectedRevision expectedRevision =
                version == -1L ? ExpectedRevision.NO_STREAM : ExpectedRevision.expectedRevision(version);

        AppendToStreamOptions options = AppendToStreamOptions.get()
                .expectedRevision(expectedRevision);

        client.appendToStream(streamName, options, preparedEvents).get();
    }

    @SneakyThrows
    @Override
    public List<Object> loadEvents(String streamId) {
        val result =
                Try.of(() -> client
                        .readStream(streamId).get()
                ).map(ReadResult::getEvents).map(List::ofAll).getOrElse(List.empty());

        return result.map(serde::deserialise);
    }
}
