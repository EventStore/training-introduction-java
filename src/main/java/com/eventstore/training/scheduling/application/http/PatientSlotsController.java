package com.eventstore.training.scheduling.application.http;

import com.eventstore.training.scheduling.domain.slot.readmodel.patientslots.PatientSlot;
import com.eventstore.training.scheduling.domain.slot.readmodel.patientslots.PatientSlotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/slots/patient")
class PatientSlotsController {
  @Autowired private PatientSlotsRepository repository;

  @GetMapping
  public List<PatientSlot> list(@RequestHeader("X-PatientId") String patientId) {
    return repository.getPatientSlots(patientId).asJava();
  }
}
