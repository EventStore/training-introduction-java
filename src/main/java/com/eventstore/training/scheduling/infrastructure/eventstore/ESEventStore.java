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
    private final StreamsClient client;
    private final EsEventSerde serde = new EsEventSerde();

    public ESEventStore(StreamsClient client) {
        this.client = client;
    }

    @Override
    public void appendEvents(String streamName, Long version, List<Object> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        java.util.List<ProposedEvent> preparedEvents = events.map(serde::serialise).asJava();

        if (version == -1L) {
            client.appendToStream(streamName, SpecialStreamRevision.NO_STREAM, preparedEvents);
        } else {
            client.appendToStream(streamName, ExpectedRevision.expectedRevision(version), preparedEvents);
        }
    }

    @SneakyThrows
    @Override
    public List<Object> loadEvents(String streamId) {
        val result =
                Try.of(() -> client
                        .readStream(Direction.Forward, streamId, StreamRevision.START, 4096, false)
                        .get()).map(ReadResult::getEvents).map(List::ofAll).getOrElse(List.empty());

        return result.map(serde::deserialise);
    }
}
