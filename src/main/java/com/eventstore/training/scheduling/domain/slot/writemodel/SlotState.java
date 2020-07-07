package com.eventstore.training.scheduling.domain.slot.writemodel;

import com.eventstore.training.scheduling.domain.slot.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Scheduled;
import com.eventstore.training.scheduling.eventsourcing.Event;
import com.eventstore.training.scheduling.eventsourcing.State;
import io.vavr.API;
import io.vavr.control.Option;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class SlotState extends State<SlotState> {
  private Option<LocalDateTime> maybeStartTime = None();
  private Option<String> maybePatientId = None();

  public void apply(Event event) {
    Match(event)
        .of(
            Case(
                $(instanceOf(Scheduled.class)),
                scheduled -> {
                  maybeStartTime = Some(scheduled.getStartTime());
                  return null;
                }),
            Case(
                $(instanceOf(Booked.class)),
                booked -> maybePatientId = Some(booked.getPatientId())),
            API.Case(API.$(instanceOf(Cancelled.class)), () -> maybePatientId = None()));
  }

  public boolean isNotScheduled() {
    return maybeStartTime.isEmpty();
  }

  public boolean isScheduled() {
    return maybeStartTime.isDefined();
  }

  public boolean isBooked() {
    return maybePatientId.isDefined();
  }

  public boolean isStarted(Clock clock) {
    return maybeStartTime
        .map(
            startTime ->
                startTime.isBefore(
                    LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault())))
        .getOrElse(false);
  }

}
