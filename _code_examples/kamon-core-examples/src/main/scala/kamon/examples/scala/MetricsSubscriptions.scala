package kamon.examples.scala

import akka.actor.{Props, Actor}
import kamon.Kamon
import kamon.metric.SubscriptionsDispatcher.TickMetricSnapshot
import kamon.metric.TraceMetrics

// tag:metrics-subscriptions:start
object MetricsSubscriptions {
  Kamon.start()
  val subscriber = kamon.actorSystem.actorOf(Props[SimplePrinter], "subscriber")

  val traceRecorder = kamon.metrics.register(TraceMetrics, "test-trace").get.recorder
  traceRecorder.ElapsedTime.record(500)
  traceRecorder.ElapsedTime.record(600)

  kamon.metrics.subscribe("trace", "test-trace", subscriber)
  kamon.metrics.subscribe("trace", "test-*", subscriber)
  kamon.metrics.subscribe("trace", "**", subscriber)

  kamon.shutdown()
}
// tag:metrics-subscriptions:end

class SimplePrinter extends Actor {
  def receive = {
    case tms: TickMetricSnapshot => println(tms)
  }
}