package com.eventstore.training.scheduling.domain.readmodel;

import com.eventstore.training.scheduling.application.PatientSlotsProjection;
import com.eventstore.training.scheduling.domain.readmodel.patientslots.PatientSlot;
import com.eventstore.training.scheduling.domain.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.writemodel.event.Scheduled;
import com.eventstore.training.scheduling.infrastructure.projections.Projection;
import com.eventstore.training.scheduling.eventsourcing.ProjectionTest;
import com.eventstore.training.scheduling.infrastructure.inmemory.InMemoryPatientSlotsRepository;
import io.vavr.collection.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class PatientSlotsProjectionTest extends ProjectionTest {
  private InMemoryPatientSlotsRepository repository;
  private String patientId;
  private final LocalDateTime now = LocalDateTime.now();
  private final String slotId = now.toString();
  private final Duration tenMinutes = Duration.ofMinutes(10L);
  private Projection projection;


  protected String randomString() {
    return UUID.randomUUID().toString().replace("-", "").substring(8);
  }

  @BeforeEach
  void beforeEach() {
    repository = new InMemoryPatientSlotsRepository();
    projection = new PatientSlotsProjection(repository);
    patientId = randomString();
  }

  @Override
  protected Projection getProjection() {
    return projection;
  }

  @Test
  void shouldReturnAnEmptyListOfSlots() {
    then(List.empty(), repository.getPatientSlots(patientId));
  }

  @Test
  void shouldReturnAnEmptyListOfSlotsIfTheSlotWasScheduled() {
    val scheduled = new Scheduled(slotId, now, tenMinutes);

    given(scheduled);
    then(List.empty(), repository.getPatientSlots(patientId));
  }

  // Test 9
  @Test
  void shouldReturnASlotIfWasBooked() {

  }

  // Test 10
  @Test
  void shouldReturnASlotIfWasCanceled() {

  }

  @Test
  void shouldReturnBothCancelledAndBooked() {

  }
}
