package com.eventstore.training.scheduling.infrastructure.eventstore;

import com.eventstore.dbclient.EventData;
import com.eventstore.dbclient.ResolvedEvent;
import com.eventstore.training.scheduling.domain.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.writemodel.event.Scheduled;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import lombok.val;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class EsEventSerde {
    final ObjectMapper objectMapper = new ObjectMapper();

    public EventData serialise(Object object) {
        UUID eventId = UUID.randomUUID();
        val data = objectMapper.createObjectNode();

        return switch (object)
           {
               case Scheduled scheduled -> {
                   data.put("slotId", scheduled.slotId());
                   data.put("startTime", scheduled.startTime().toString());
                   data.put("duration", scheduled.duration().toString());
                   yield toEventData("scheduled", eventId, data);
               }
               case Cancelled cancelled -> {
                   data.put("slotId", cancelled.slotId());
                   data.put("reason", cancelled.reason());
                   yield toEventData("cancelled", eventId, data);
               }
               case Booked booked -> {
                   data.put("slotId", booked.slotId());
                   data.put("patientId", booked.patientId());
                   yield toEventData("booked", eventId, data);
               }
               default -> throw new IllegalStateException("Unexpected value: %s".formatted(object));
           };
    }

    private EventData toEventData(String eventType, UUID eventId, ObjectNode dataNode) {
        try {
            return new EventData(
                    eventId,
                    eventType,
                    "application/json",
                    objectMapper.writeValueAsBytes(dataNode),
                    objectMapper.writeValueAsBytes(objectMapper.createObjectNode()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public Object deserialise(ResolvedEvent event) {
        val data = objectMapper.readTree(event.getEvent().getEventData());
        val eventType = event.getEvent().getEventType();

        return switch (eventType){
            case "scheduled" ->
                new Scheduled(
                    data.get("slotId").asText(),
                    LocalDateTime.parse(data.get("startTime").asText()),
                    Duration.parse(data.get("duration").asText())
                );
            case "booked" ->
                new Booked(
                    data.get("slotId").asText(),
                    data.get("patientId").asText()
                );
            case "cancelled" ->
                new Cancelled(
                    data.get("slotId").asText(),
                    data.get("reason").asText()
                );
            default -> throw new IllegalStateException("Unexpected value: %s".formatted(eventType));
        };
    }
}
