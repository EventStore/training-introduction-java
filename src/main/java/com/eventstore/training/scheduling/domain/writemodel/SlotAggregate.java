package com.eventstore.training.scheduling.domain.writemodel;

import com.eventstore.training.scheduling.domain.writemodel.error.*;
import com.eventstore.training.scheduling.domain.writemodel.event.Booked;
import com.eventstore.training.scheduling.domain.writemodel.event.Cancelled;
import com.eventstore.training.scheduling.domain.writemodel.event.Scheduled;
import com.eventstore.training.scheduling.eventsourcing.AggregateRoot;

import java.time.Duration;
import java.time.LocalDateTime;

public class SlotAggregate extends AggregateRoot {
    private boolean isBooked = false;
    private boolean isScheduled = false;
    private LocalDateTime startTime = null;

    public SlotAggregate() {
        register(Booked.class, this::when);
        register(Cancelled.class, this::when);
        register(Scheduled.class, this::when);
    }

    public void schedule(String id, LocalDateTime startTime, Duration duration) {
        if (isScheduled) {
            throw new SlotAlreadyScheduled();
        }

        raise(new Scheduled(id, startTime, duration));
    }

    public void cancel(String reason, LocalDateTime cancellationTime) {
        if (!isBooked) {
            throw new SlotNotBooked();
        }

        if (isStarted(cancellationTime)) {
            throw new SlotAlreadyStarted();
        }

        if (isBooked && !isStarted(cancellationTime)) {
            raise(new Cancelled(getId(), reason));
        }
    }

    public void book(String patientId) {
        if (!isScheduled) {
            throw new SlotNotScheduled();
        }

        if (isBooked) {
            throw new SlotAlreadyBooked();
        }

        raise(new Booked(getId(), patientId));
    }

    private boolean isStarted(LocalDateTime cancellationTime) {
        return cancellationTime.isAfter(startTime);
    }

    private void when(Booked booked) {
        isBooked = true;
    }

    private void when(Cancelled cancelled) {
        isBooked = false;
    }

    private void when(Scheduled scheduled) {
        isScheduled = true;
        startTime = scheduled.startTime();
        setId(scheduled.slotId());
    }
}
