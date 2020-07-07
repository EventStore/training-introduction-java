package com.eventstore.training.scheduling;

import com.eventstore.dbclient.StreamsClient;
import com.eventstore.dbclient.Timeouts;
import com.eventstore.dbclient.UserCredentials;
import com.eventstore.training.scheduling.application.projector.AvailableSlotsProjector;
import com.eventstore.training.scheduling.application.projector.PatientSlotsProjector;
import com.eventstore.training.scheduling.domain.slot.readmodel.availableslots.AvailableSlotsRepository;
import com.eventstore.training.scheduling.domain.slot.readmodel.patientslots.PatientSlotsRepository;
import com.eventstore.training.scheduling.eventsourcing.*;
import com.eventstore.training.scheduling.infrastructure.eventstore.ESEventStore;
import com.eventstore.training.scheduling.infrastructure.eventstore.EsEventSerde;
import com.eventstore.training.scheduling.infrastructure.inmemory.InMemoryAvailableSlotsRepository;
import com.eventstore.training.scheduling.infrastructure.inmemory.InMemoryPatientSlotsRepository;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.vavr.collection.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.net.ssl.SSLException;
import java.time.Clock;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AppConfig {
  private final EventStore eventStore;
  private final EventStreamSubscription subscription;
  private final InMemoryAvailableSlotsRepository availableSlotsRepository;
  private final InMemoryPatientSlotsRepository patientSlotsRepository;

  public AppConfig() throws SSLException {
    SslContext sslContext =
        GrpcSslContexts.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
    StreamsClient client =
        new StreamsClient(
            "localhost",
            2113,
            new UserCredentials("admin", "changeit"),
            Timeouts.DEFAULT,
            sslContext);
    eventStore = new ESEventStore(client, new EsEventSerde());
    availableSlotsRepository = new InMemoryAvailableSlotsRepository();
    patientSlotsRepository = new InMemoryPatientSlotsRepository();
    subscription =
        new EventStreamSubscription(
            List.of(
                new AvailableSlotsProjector(availableSlotsRepository),
                new PatientSlotsProjector(patientSlotsRepository)),
            eventStore, "slot");
    startSubscription();
  }

  @Bean
  public CommandHandler commandHandler() {
    return new CommandHandler(
        new AggregateStore(eventStore, Clock.systemUTC()));
  }

  @Bean
  public AvailableSlotsRepository availableSlotsRepository() {
    return availableSlotsRepository;
  }

  @Bean
  public PatientSlotsRepository patientSlotsRepository() {
    return patientSlotsRepository;
  }

  @Bean(name = "fixedThreadPool")
  public Executor fixedThreadPool() {
    return Executors.newFixedThreadPool(2);
  }

  @Async("fixedThreadPool")
  public void startSubscription() {
    subscription.run();
  }
}
