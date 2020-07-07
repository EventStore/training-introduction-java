package com.eventstore.training.scheduling.domain.slot.readmodel;

import com.eventstore.training.scheduling.application.projector.AvailableSlotsProjector;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.slot.writemodel.event.Scheduled;
import com.eventstore.training.scheduling.domain.slot.readmodel.availableslots.AvailableSlot;
import com.eventstore.training.scheduling.eventsourcing.EventHandlerTest;
import com.eventstore.training.scheduling.infrastructure.inmemory.InMemoryAvailableSlotsRepository;
import io.vavr.collection.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AvailableSlotsProjectorTest extends EventHandlerTest {
  private InMemoryAvailableSlotsRepository repository;
  private final LocalDateTime now = LocalDateTime.now();
  private final String slotId = now.toString();
  private final Duration tenMinutes = Duration.ofMinutes(10L);

  @BeforeEach
  void beforeEach() {
    repository = new InMemoryAvailableSlotsRepository();
    handler = new AvailableSlotsProjector(repository);
  }

  @Test
  void shouldAddSlotToTheList() {
    val scheduled = new Scheduled(slotId, now, tenMinutes);

    given(scheduled);
    then(
        List.of(
            new AvailableSlot(
                scheduled.getSlotId(), scheduled.getStartTime(), scheduled.getDuration())),
        repository.getSlotsAvailableOn(LocalDate.now()));
  }

  @Test
  void shouldRemoveSlotFromTheListIfItWasBooked() {
    val scheduled = new Scheduled(slotId, now, tenMinutes);
    val booked = new Booked(scheduled.getSlotId(), randomString());

    given(scheduled, booked);
    then(List.empty(), repository.getSlotsAvailableOn(LocalDate.now()));
  }

  @Test
  void shouldAddSlotAgainIfBookingWasCancelled() {
    val scheduled = new Scheduled(slotId, now, tenMinutes);
    val booked = new Booked(scheduled.getSlotId(), randomString());
    val cancelled = new Cancelled(scheduled.getSlotId(), randomString());

    given(scheduled, booked, cancelled);
    then(
        List.of(
            new AvailableSlot(
                scheduled.getSlotId(), scheduled.getStartTime(), scheduled.getDuration())),
        repository.getSlotsAvailableOn(LocalDate.now()));
  }
}
