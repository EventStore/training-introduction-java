package com.eventstore.training.scheduling.controllers;

import lombok.NonNull;

public record PostBook(
    @NonNull String patientId
) { }
