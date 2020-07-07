package com.eventstore.training.scheduling.application.projector;

import com.eventstore.training.scheduling.domain.slot.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Scheduled;
import com.eventstore.training.scheduling.domain.slot.readmodel.patientslots.AvailableSlot;
import com.eventstore.training.scheduling.domain.slot.readmodel.patientslots.PatientSlotsRepository;
import com.eventstore.training.scheduling.eventsourcing.Event;
import com.eventstore.training.scheduling.eventsourcing.EventHandler;
import com.eventstore.training.scheduling.eventsourcing.EventMetadata;
import io.vavr.API;

import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

public class PatientSlotsProjector implements EventHandler {
  private final PatientSlotsRepository repository;

  public PatientSlotsProjector(PatientSlotsRepository repository) {
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
                  repository.markAsBooked(booked.getSlotId(), booked.getPatientId());
                  return null;
                }),
            API.Case(
                API.$(instanceOf(Cancelled.class)),
                cancelled -> {
                  repository.markAsCancelled(cancelled.getSlotId());
                  return null;
                }));
  }
}
