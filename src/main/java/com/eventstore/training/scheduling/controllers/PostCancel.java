package com.eventstore.training.scheduling.controllers;

import lombok.NonNull;

public record PostCancel(
  @NonNull String reason
) { }
