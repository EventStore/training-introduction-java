package com.eventstore.training.scheduling.infrastructure.inmemory;

import com.eventstore.training.scheduling.domain.readmodel.patientslots.AvailableSlot;
import com.eventstore.training.scheduling.domain.readmodel.patientslots.PatientSlot;
import com.eventstore.training.scheduling.domain.readmodel.patientslots.PatientSlotsRepository;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;
import lombok.val;

public class InMemoryPatientSlotsRepository implements PatientSlotsRepository {
  private List<AvailableSlot> available;
  private Map<String, List<PatientSlot>> patientSlots;

  public InMemoryPatientSlotsRepository() {
    this.available = List.empty();
    this.patientSlots = HashMap.empty();
  }

  @Override
  public void add(AvailableSlot slot) {
    available = available.append(slot);
  }

  @Override
  public void markAsBooked(String slotId, String patientId) {
  }

  @Override
  public void markAsCancelled(String slotId) {
  }

  @Override
  public List<PatientSlot> getPatientSlots(String patientId) {
    return patientSlots.getOrElse(patientId, List.empty());
  }
}
