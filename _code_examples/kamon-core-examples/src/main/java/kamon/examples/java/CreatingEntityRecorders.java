package kamon.examples.java;


import kamon.Kamon;
import kamon.metric.Entity;
import kamon.metric.EntityRecorderFactory;
import kamon.metric.EntityRegistration;
import kamon.metric.GenericEntityRecorder;
import kamon.metric.instrument.*;
import scala.Option;

public class CreatingEntityRecorders {

  public static void main(String[] args) {
    final Kamon kamon = Kamon.create();

    // tag:entity-registration:start
    //
    // Managed registration.
    //

    final Option<EntityRegistration<ActorMetrics>> managedRegistration = kamon.metrics().register(ActorMetrics.Factory, "my-managed-actor");

    if(managedRegistration.nonEmpty()) {
      // The entity was successfully registered.
      final ActorMetrics managedRecorder = managedRegistration.get().recorder();
      managedRecorder.processingTime().record(42);
    }

    //
    // Manual registration.
    //

    final Entity myManualActor = Entity.create("my-manual-actor", ActorMetrics.Category);
    final InstrumentFactory instrumentFactory = kamon.metrics().instrumentFactory(ActorMetrics.Category);
    final ActorMetrics manualRecorder = kamon.metrics().register(myManualActor,
        new ActorMetrics(instrumentFactory)).recorder();

    manualRecorder.processingTime().record(42);
    // tag:entity-registration:end


    kamon.shutdown();
  }


  // tag:creating-entity-recorders:start
  /**
   *  Besides the inherent verbosity of Java, this code does exactly the same
   *  as it's Scala counter part.
   */
  public static class ActorMetrics extends GenericEntityRecorder {
    private final Histogram timeInMailbox;
    private final Histogram processingTime;
    private final MinMaxCounter mailboxSize;
    private final Counter errors;

    public ActorMetrics(InstrumentFactory instrumentFactory) {
      super(instrumentFactory);

      timeInMailbox = histogram("time-in-mailbox", Time.Nanoseconds());
      processingTime = histogram("processing-time", Time.Nanoseconds());
      mailboxSize = minMaxCounter("mailbox-size");
      errors = counter("errors");
    }

    public Histogram timeInMailbox() {
      return timeInMailbox;
    }

    public Histogram processingTime() {
      return processingTime;
    }

    public MinMaxCounter mailboxSize() {
      return mailboxSize;
    }

    public Counter errors() {
      return errors;
    }

    static final String Category = "actor";
    static final EntityRecorderFactory<ActorMetrics> Factory = new EntityRecorderFactory<ActorMetrics>() {

      @Override
      public String category() {
        return Category;
      }

      @Override
      public ActorMetrics createRecorder(InstrumentFactory instrumentFactory) {
        return new ActorMetrics(instrumentFactory);
      }
    };
  }
  // tag:creating-entity-recorders:end
}
