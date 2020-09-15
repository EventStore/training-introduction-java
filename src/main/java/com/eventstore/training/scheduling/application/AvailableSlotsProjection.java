package com.eventstore.training.scheduling.application;

import com.eventstore.training.scheduling.domain.readmodel.availableslots.AvailableSlot;
import com.eventstore.training.scheduling.domain.readmodel.availableslots.AvailableSlotsRepository;
import com.eventstore.training.scheduling.domain.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.writemodel.event.Scheduled;
import com.eventstore.training.scheduling.infrastructure.projections.Projection;

public class AvailableSlotsProjection extends Projection {
    public AvailableSlotsProjection(AvailableSlotsRepository repository) {
        when(Scheduled.class, scheduled -> repository.add(null));

//        when(Booked.class, );

    }
}
