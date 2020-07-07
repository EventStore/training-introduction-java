package com.eventstore.training.scheduling.application.http;

import lombok.Data;
import lombok.NonNull;

@Data
public class PostBook {
  public @NonNull String patientId;
}
