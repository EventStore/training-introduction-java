package com.eventstore.training.scheduling.domain.slot.writemodel.error;

import com.eventstore.training.scheduling.eventsourcing.Error;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class SlotNotBooked extends Error {}
