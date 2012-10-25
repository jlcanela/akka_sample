package akka.cluster

import com.typesafe.config.ConfigFactory
import akka.remote.testkit.MultiNodeConfig
import akka.remote.testkit.MultiNodeSpec
import akka.testkit._
import akka.util.duration._
import akka.cluster.MemberStatus._

object ClusterDemoMultiJvmSpec extends MultiNodeConfig {
  // register the named roles (nodes) of the test
  val first = role("first")
  val second = role("second")
  val third = role("third")

  // this configuration will be used for all nodes
  // note that no fixed host names and ports are used
  commonConfig(ConfigFactory.parseString(
    "akka.cluster.auto-join = off"))
}

// need one concrete test class per node
class ClusterDemoMultiJvmNode1 extends ClusterDemoSpec
class ClusterDemoMultiJvmNode2 extends ClusterDemoSpec
class ClusterDemoMultiJvmNode3 extends ClusterDemoSpec

abstract class ClusterDemoSpec
  extends MultiNodeSpec(ClusterDemoMultiJvmSpec) {

  import ClusterDemoMultiJvmSpec._

  override def initialParticipants = roles.size

  val firstAddress = node(first).address
  val secondAddress = node(second).address
  val thirdAddress = node(third).address

  "A cluster demo" must {
    "illustrate how to start up first node" in {

      runOn(first) {
        // this will only run on the 'first' node

        Cluster(system) join firstAddress
        // verify that single node becomes member
        awaitCond(Cluster(system).latestGossip.members.
          exists(m ⇒
          m.address == firstAddress && m.status == Up))
      }

      // this will run on all nodes
      // use barrier to coordinate test steps
      testConductor.enter("first-started")
    }

    "illustrate join more nodes" in within(10 seconds) {
      runOn(second, third) {
        Cluster(system) join firstAddress
      }

      val expected =
        Set(firstAddress, secondAddress, thirdAddress)
      // on all nodes, verify that all becomes members
      awaitCond(
        Cluster(system).latestGossip.members.
          map(_.address) == expected)
      // and shifted to status Up
      awaitCond(
        Cluster(system).latestGossip.members.
          forall(_.status == Up))

      testConductor.enter("all-joined")
    }

    "illustrate failure detection" in within(10 seconds) {
      runOn(first) {
        // this will System.exit on another node
        testConductor.shutdown(second, 0)
      }

      testConductor.enter("second-shutdown")

      runOn(first, third) {
        // verify that other nodes detect the crash
        awaitCond(
          Cluster(system).latestGossip.overview.
            unreachable.exists(
            _.address == secondAddress))
      }

      testConductor.enter("thats-it")
    }
  }

}