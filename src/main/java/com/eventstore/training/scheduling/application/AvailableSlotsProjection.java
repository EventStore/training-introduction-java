package com.eventstore.training.scheduling.application;

import com.eventstore.training.scheduling.domain.readmodel.availableslots.AvailableSlot;
import com.eventstore.training.scheduling.domain.readmodel.availableslots.AvailableSlotsRepository;
import com.eventstore.training.scheduling.domain.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.writemodel.event.Scheduled;
import com.eventstore.training.scheduling.infrastructure.projections.Projection;

public class AvailableSlotsProjection extends Projection {
    public AvailableSlotsProjection(AvailableSlotsRepository repository) {
        when(Scheduled.class, scheduled -> repository.add(
                    new AvailableSlot(
                            scheduled.slotId(),
                            scheduled.startTime(),
                            scheduled.duration())));

        when(Booked.class, booked -> repository.markAsUnavailable(booked.slotId()));

        when(Cancelled.class, cancelled -> repository.markAsAvailable(cancelled.slotId()));
    }
}
