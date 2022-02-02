package com.eventstore.training.scheduling.application;

import com.eventstore.training.scheduling.domain.readmodel.patientslots.AvailableSlot;
import com.eventstore.training.scheduling.domain.readmodel.patientslots.PatientSlotsRepository;
import com.eventstore.training.scheduling.domain.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.writemodel.event.Scheduled;
import com.eventstore.training.scheduling.infrastructure.projections.Projection;

public class PatientSlotsProjection extends Projection {

    public PatientSlotsProjection(PatientSlotsRepository repository) {
        when(Scheduled.class, scheduled -> repository.add(
                new AvailableSlot(
                        scheduled.slotId(),
                        scheduled.startTime(),
                        scheduled.duration()))
        );

//        when(Booked.class, booked -> repository.markAsBooked(booked.slotId(), booked.patientId()));
//
//        when(Cancelled.class, cancelled -> repository.markAsCancelled(cancelled.slotId()));
    }
}
