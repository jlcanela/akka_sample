package sample.cluster.simple

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import scala.concurrent.util.Duration._
import scala.concurrent.util.Duration
import us.theatr.akka.quartz.{AddCronSchedule, QuartzActor}
import akka.pattern.throttle.TimerBasedThrottler
import akka.pattern.throttle.Throttler._

object SimpleClusterApp {

  def main(args: Array[String]): Unit = {

    // Override the configuration of the port 
    // when specified as program argument
    if (args.nonEmpty) System.setProperty("akka.remote.netty.port", args(0))

    val system = ActorSystem("ClusterSystem")

    val displayActor = system.actorOf(Props(new Actor with ActorLogging {
      def receive = {
        case "QUARTZ" ⇒
          log.info("received QUARTZ")
      }
    }), name = "displayActor")

    val quartzActor = system.actorOf(Props[QuartzActor])
    quartzActor ! AddCronSchedule(displayActor, "0/5 * * * * ?", "QUARTZ")

    // Create an Akka system
    val clusterListener = system.actorOf(Props(new Actor with ActorLogging {
      def receive = {
        case state: CurrentClusterState ⇒
          log.info("Current members: {}", state.members)
        case MemberJoined(member) ⇒
          log.info("Member joined: {}", member)
        case MemberUp(member) ⇒
          log.info("Member is Up: {}", member)
        case MemberUnreachable(member) ⇒
          log.info("Member detected as unreachable: {}", member)
        case _: ClusterDomainEvent ⇒ // ignore

      }
    }), name = "clusterListener")

    Cluster(system).subscribe(clusterListener, classOf[ClusterDomainEvent])

    // A simple actor that prints whatever it receives
    val printer = system.actorOf(Props(new Actor {
      def receive = {
        case x => println(x)
      }
    }))

    // The throttler for this example, setting the rate
    val throttler = system.actorOf(Props(new TimerBasedThrottler(3 msgsPer (Duration(5, "second")))))

    // Set the target
    throttler ! SetTarget(Some(printer))

    // These three messages will be sent to the printer immediately
    throttler ! Queue("1")
    throttler ! Queue("2")
    throttler ! Queue("3")

    // These two will wait at least until 1 second has passed
    throttler ! Queue("4")
    throttler ! Queue("5")
    throttler ! Queue("6")

    throttler ! Queue("7")
  }

}