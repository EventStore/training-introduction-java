package com.eventstore.training.scheduling.domain.slot.writemodel;

import com.eventstore.training.scheduling.domain.slot.writemodel.command.Book;
import com.eventstore.training.scheduling.domain.slot.writemodel.command.Cancel;
import com.eventstore.training.scheduling.domain.slot.writemodel.command.Schedule;
import com.eventstore.training.scheduling.domain.slot.writemodel.error.*;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Scheduled;
import com.eventstore.training.scheduling.eventsourcing.AggregateTest;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class SlotAggregateTest extends AggregateTest<SlotAggregate> {

  private final LocalDateTime now = LocalDateTime.now(clock);
  private final String slotId = now.toString();
  private final Duration tenMinutes = Duration.ofMinutes(10L);
  @Override
  public SlotAggregate newInstance() {
    return new SlotAggregate(slotId, clock);
  }

  @Test
  void canBeScheduled() {
    Schedule command = new Schedule(now, tenMinutes);
    when(command);
    then(new Scheduled(slotId, command.startTime, command.duration));
  }

  @Test
  void cantBeDoubleScheduled() {
    given(new Scheduled(slotId, now, tenMinutes));
    Schedule command = new Schedule(now, tenMinutes);
    when(command);
    then(new SlotAlreadyScheduled());
  }

  @Test
  void canBeBooked() {
    Scheduled scheduled = new Scheduled(slotId, now, tenMinutes);
    given(scheduled);
    val command = new Book(randomString());
    when(command);
    then(new Booked(scheduled.getSlotId(), command.patientId));
  }

  @Test
  void cantBeBookedIfWasNotScheduled() {
    when(new Book(randomString()));
    then(new SlotNotScheduled());
  }

  @Test
  void cantBeDoubleBooked() {
    Scheduled scheduled = new Scheduled(slotId, now, tenMinutes);
    Booked booked = new Booked(scheduled.getSlotId(), randomString());
    given(scheduled, booked);
    when(new Book(randomString()));
    then(new SlotAlreadyBooked());
  }

  @Test
  void canBeCancelled() {
    Scheduled scheduled = new Scheduled(slotId, now, tenMinutes);
    Booked booked = new Booked(scheduled.getSlotId(), randomString());

    given(scheduled, booked);
    when(new Cancel("No longer needed"));
    then(new Cancelled(scheduled.getSlotId(), new Cancel("No longer needed").reason));
  }

  @Test
  void cancelledSlotCanBeBookedAgain() {
    Scheduled scheduled = new Scheduled(slotId, now, tenMinutes);
    Booked booked = new Booked(scheduled.getSlotId(), randomString());
    Cancelled cancelled = new Cancelled(scheduled.getSlotId(), randomString());
    Book command = new Book(randomString());

    given(scheduled, booked, cancelled);
    when(command);
    then(new Booked(scheduled.getSlotId(), command.patientId));
  }

  @Test
  void cantBeCancelledAfterStartTime() {
    Scheduled scheduled =
        new Scheduled(slotId, now.minusHours(1), tenMinutes);
    Booked booked = new Booked(scheduled.getSlotId(), randomString());

    given(scheduled, booked);
    when(new Cancel("No longer needed"));
    then(new SlotAlreadyStarted());
  }

  @Test
  void cantBeCancelledIfWasntBooked() {
    given(new Scheduled(slotId, now, tenMinutes));
    when(new Cancel("No longer needed"));
    then(new SlotNotBooked());
  }
}
