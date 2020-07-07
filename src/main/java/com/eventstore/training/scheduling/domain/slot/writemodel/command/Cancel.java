package com.eventstore.training.scheduling.domain.slot.writemodel.command;

import com.eventstore.training.scheduling.eventsourcing.Command;
import lombok.Data;
import lombok.NonNull;

@Data
public class Cancel implements Command {
  public final @NonNull String reason;
}
