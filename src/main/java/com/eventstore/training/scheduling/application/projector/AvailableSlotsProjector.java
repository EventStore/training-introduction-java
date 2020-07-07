package com.eventstore.training.scheduling.application.projector;

import com.eventstore.training.scheduling.domain.slot.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Scheduled;
import com.eventstore.training.scheduling.domain.slot.readmodel.availableslots.AvailableSlot;
import com.eventstore.training.scheduling.domain.slot.readmodel.availableslots.AvailableSlotsRepository;
import com.eventstore.training.scheduling.eventsourcing.Event;
import com.eventstore.training.scheduling.eventsourcing.EventHandler;
import com.eventstore.training.scheduling.eventsourcing.EventMetadata;
import io.vavr.API;

import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

public class AvailableSlotsProjector implements EventHandler {
  private final AvailableSlotsRepository repository;

  public AvailableSlotsProjector(AvailableSlotsRepository repository) {
    this.repository = repository;
  }

  @Override
  public void handle(Event event, EventMetadata eventMetadata) {
    Match(event)
        .of(
            API.Case(
                API.$(instanceOf(Scheduled.class)),
                scheduled -> {
                  repository.add(
                      new AvailableSlot(
                          scheduled.getSlotId(),
                          scheduled.getStartTime(),
                          scheduled.getDuration()));
                  return null;
                }),
            API.Case(
                API.$(instanceOf(Booked.class)),
                booked -> {
                  repository.markAsUnavailable(booked.getSlotId());
                  return null;
                }),
            API.Case(
                API.$(instanceOf(Cancelled.class)),
                cancelled -> {
                  repository.markAsAvailable(cancelled.getSlotId());
                  return null;
                }));
  }
}
