package com.eventstore.training.scheduling.infrastructure.eventstore;

import com.eventstore.dbclient.*;
import com.eventstore.training.scheduling.eventsourcing.Event;
import com.eventstore.training.scheduling.eventsourcing.EventEnvelope;
import com.eventstore.training.scheduling.eventsourcing.EventStore;
import io.grpc.StatusRuntimeException;
import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ESEventStore implements EventStore {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final StreamsClient client;
  private final EsEventSerde eventSerde;

  public ESEventStore(StreamsClient client, EsEventSerde eventSerde) {
    this.client = client;
    this.eventSerde = eventSerde;
  }

  @Override
  public List<EventEnvelope> readFromStream(String streamId, Long fromPosition) {
    try {
      CompletableFuture<ReadResult> readResultCompletableFuture =
          client.readStream(Direction.Forward, streamId, new StreamRevision(fromPosition), 2048, true);
      return List.ofAll(readResultCompletableFuture.get().getEvents())
          .map(eventSerde::deserialise)
          .flatMap(x -> x);
    } catch (InterruptedException | ExecutionException e) {
      return List.empty();
    }
  }

  @Override
  public List<EventEnvelope> readAll(Long fromPosition) {
    try {
      return List.ofAll(
              client
                  .readAll(Direction.Forward, new Position(fromPosition, fromPosition), 2048, false)
                  .get()
                  .getEvents())
          .map(eventSerde::deserialise)
          .flatMap(x -> x);
    } catch (StatusRuntimeException e) {
      logger.warn(
          "Failed to read events from position {} due to: {}", fromPosition, e.getMessage());
      return List.empty();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return List.empty();
  }

  @Override
  public void createNewStream(String streamId, List<Event> events) {
    client.appendToStream(
        streamId,
        SpecialStreamRevision.NO_STREAM,
        events.map(eventSerde::serialise).asJava());
  }

  @Override
  public void appendToStream(
      String streamId, Long expectedVersion, List<Event> events) {
    client.appendToStream(
        streamId,
        new StreamRevision(expectedVersion),
        events.map(eventSerde::serialise).asJava());
  }
}
