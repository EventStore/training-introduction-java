package com.eventstore.training.scheduling.domain.writemodel.command;

import com.eventstore.training.scheduling.domain.writemodel.SlotAggregate;
import com.eventstore.training.scheduling.eventsourcing.AggregateStore;
import com.eventstore.training.scheduling.infrastructure.commands.CommandHandler;
import lombok.val;

public class Handlers extends CommandHandler {
    public Handlers(AggregateStore aggregateStore) {
        register(Schedule.class, schedule -> {
            val aggregate = aggregateStore.load(SlotAggregate.class, schedule.id());
            aggregate.schedule(schedule.id(), schedule.startTime(), schedule.duration());
            aggregateStore.save(aggregate);
        });
        register(Book.class, book -> {
//            val aggregate = aggregateStore.load(SlotAggregate.class, book.id());
//            aggregate.book(book.patientId());
//            aggregateStore.save(aggregate);
        });
        register(Cancel.class, cancel -> {
//            val aggregate = aggregateStore.load(SlotAggregate.class, cancel.id());
//            aggregate.cancel(cancel.reason(), cancel.cancellationTime());
//            aggregateStore.save(aggregate);
        });
    }
}
