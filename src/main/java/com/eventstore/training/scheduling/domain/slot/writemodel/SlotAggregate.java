package com.eventstore.training.scheduling.domain.slot.writemodel;

import com.eventstore.training.scheduling.domain.slot.writemodel.command.Book;
import com.eventstore.training.scheduling.domain.slot.writemodel.command.Cancel;
import com.eventstore.training.scheduling.domain.slot.writemodel.command.Schedule;
import com.eventstore.training.scheduling.domain.slot.writemodel.error.*;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Scheduled;
import com.eventstore.training.scheduling.eventsourcing.Error;
import com.eventstore.training.scheduling.eventsourcing.*;
import io.vavr.collection.List;

import java.time.Clock;

import static io.vavr.API.*;
import static io.vavr.Predicates.allOf;
import static io.vavr.Predicates.instanceOf;

public class SlotAggregate extends Aggregate<SlotAggregate, SlotState> {
  private final Clock clock;

  public SlotAggregate(String id, Clock clock) {
    super(id, new SlotState());
    this.clock = clock;
  }

  @Override
  public List<? extends Event> calculateChanges(Command command) throws Error {
    return Match(command)
        .of(
            Case(
                $(allOf(instanceOf(Schedule.class), (x -> state.isScheduled()))),
                () -> {
                  throw new SlotAlreadyScheduled();
                }),
            Case(
                $(instanceOf(Schedule.class)),
                schedule ->
                    List.of(
                        new Scheduled(
                            getId(), schedule.startTime, schedule.duration))),
            Case(
                $(x -> state.isNotScheduled()),
                () -> {
                  throw new SlotNotScheduled();
                }),
            Case(
                $(allOf(instanceOf(Book.class), (x -> state.isBooked()))),
                () -> {
                  throw new SlotAlreadyBooked();
                }),
            Case(
                $(instanceOf(Book.class)),
                schedule ->
                    List.of(
                        new Booked(getId(), schedule.patientId))),
            Case(
                $(allOf(instanceOf(Cancel.class), (x -> state.isStarted(clock)))),
                () -> {
                  throw new SlotAlreadyStarted();
                }),
            Case(
                $(allOf(instanceOf(Cancel.class), (x -> !state.isBooked()))),
                () -> {
                  throw new SlotNotBooked();
                }),
            Case(
                $(instanceOf(Cancel.class)),
                schedule ->
                    List.of(
                        new Cancelled(getId(), schedule.reason))));
  }
}
