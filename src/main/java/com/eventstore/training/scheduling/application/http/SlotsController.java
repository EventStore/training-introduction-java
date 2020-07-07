package com.eventstore.training.scheduling.application.http;

import com.eventstore.training.scheduling.domain.slot.writemodel.SlotAggregate;
import com.eventstore.training.scheduling.domain.slot.writemodel.command.Book;
import com.eventstore.training.scheduling.domain.slot.writemodel.command.Cancel;
import com.eventstore.training.scheduling.domain.slot.writemodel.command.Schedule;
import com.eventstore.training.scheduling.eventsourcing.CommandHandler;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/slots")
class SlotsController {
  @Autowired private CommandHandler commandHandler;

  @PostMapping
  public ResponseEntity<?> schedule(@RequestBody PostSchedule schedule) {
    val command = new Schedule(schedule.startDateTime, schedule.duration);
    String aggregateId = schedule.startDateTime.toString();
    val result = commandHandler.handle(SlotAggregate.class, aggregateId, command);

    if (result.isSuccess()) {
      return ResponseEntity.status(HttpStatus.CREATED)
          .header("Location", "/slots/" + aggregateId)
          .body(null);
    } else if (result.isFailure()) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(result.getCause().getMessage());
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  }

  @PostMapping(value = "/{aggregateId}/book")
  public ResponseEntity<?> book(
      @RequestBody PostBook book,
      @PathVariable("aggregateId") String aggregateId) {
    val command = new Book(book.patientId);
    val result =
        commandHandler.handle(
            SlotAggregate.class, aggregateId, command);

    if (result.isSuccess()) {
      return ResponseEntity.status(HttpStatus.OK)
          .header("Location", "/slots/" + aggregateId)
          .body(null);
    } else if (result.isFailure()) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(result.getCause().getMessage());
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  }

  @PostMapping(value = "/{aggregateId}/cancel")
  public ResponseEntity<?> cancel(
      @RequestBody PostCancel cancel,
      @PathVariable("aggregateId") String aggregateId) {
    val command = new Cancel(cancel.reason);
    val result =
        commandHandler.handle(
            SlotAggregate.class, aggregateId, command);

    if (result.isSuccess()) {
      return ResponseEntity.status(HttpStatus.OK)
          .header("Location", "/slots/" + aggregateId)
          .body(null);
    } else if (result.isFailure()) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(result.getCause().getMessage());
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  }
}
