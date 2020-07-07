package com.eventstore.training.scheduling.application.http;

import lombok.Data;
import lombok.NonNull;

@Data
public class PostCancel {
  public @NonNull String reason;
}
