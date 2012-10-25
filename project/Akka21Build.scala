import sbt._
import sbt.Keys._

object Akka21Build extends Build {

  lazy val akka21 = Project(
    id = "akka21",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "akka21",
      organization := "org.ansoft",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2"
      // add other settings here
    )
  )
}
