package com.eventstore.training.scheduling.controllers;

import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;

public record PostSchedule(
    @NonNull LocalDateTime startDateTime,
    @NonNull Duration duration
) { }
