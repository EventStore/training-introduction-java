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
    PatientSlot updated =
        available
            .filter(slot -> slot.slotId().equals(slotId))
            .map(
                available ->
                    new PatientSlot(
                        available.slotId(),
                        available.startTime(),
                        available.duration(),
                        "booked"))
            .head();
    patientSlots =
        patientSlots.put(
            patientId, patientSlots.getOrElse(patientId, List.empty()).append(updated));
    available = available.filter(slot -> !slot.slotId().equals(slotId));
  }

  @Override
  public void markAsCancelled(String slotId) {
    val tuple =
        patientSlots
            .filterValues(slots -> slots.filter(slot -> slot.slotId().equals(slotId)).nonEmpty())
            .mapValues(Traversable::head)
            .head();
    val patientId = tuple._1;
    val cancelledSlot = tuple._2;

    patientSlots =
        patientSlots.put(
            patientId,
            patientSlots
                .getOrElse(patientId, List.empty())
                .map(
                    slot -> {
                      if (slot.slotId().equals(slotId)) {
                        return new PatientSlot(
                            slot.slotId(), slot.startTime(), slot.duration(), "cancelled");
                      } else {
                        return slot;
                      }
                    }));

    available =
        available.append(
            new AvailableSlot(
                cancelledSlot.slotId(),
                cancelledSlot.startTime(),
                cancelledSlot.duration()));
  }

  @Override
  public List<PatientSlot> getPatientSlots(String patientId) {
    return patientSlots.getOrElse(patientId, List.empty());
  }
}
