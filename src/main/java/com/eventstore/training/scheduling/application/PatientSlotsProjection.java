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
                        scheduled.getSlotId(),
                        scheduled.getStartTime(),
                        scheduled.getDuration()))
        );
    }
}
