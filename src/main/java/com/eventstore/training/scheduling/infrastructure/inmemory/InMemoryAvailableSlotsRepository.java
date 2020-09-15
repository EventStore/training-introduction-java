package com.eventstore.training.scheduling.infrastructure.inmemory;

import com.eventstore.training.scheduling.domain.readmodel.availableslots.AvailableSlot;
import com.eventstore.training.scheduling.domain.readmodel.availableslots.AvailableSlotsRepository;
import io.vavr.collection.List;

import java.time.LocalDate;

public class InMemoryAvailableSlotsRepository implements AvailableSlotsRepository {
  private List<AvailableSlot> available;
  private List<AvailableSlot> booked;

  public InMemoryAvailableSlotsRepository() {
    this.available = List.empty();
    this.booked = List.empty();
  }

  @Override
  public void add(AvailableSlot slot) {
    available = available.append(slot);
  }

  @Override
  public void markAsUnavailable(String slotId) {
  }

  @Override
  public void markAsAvailable(String slotId) {
  }

  @Override
  public List<AvailableSlot> getSlotsAvailableOn(LocalDate date) {
    return available.filter(slot -> slot.getStartTime().toLocalDate().isEqual(date));
  }
}
