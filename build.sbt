name := "My Project"

version := "1.0"

scalaVersion := "2.10.0-M7"

//resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Repository" at "http://repo.akka.io/snapshots/"

// akka-snapshots-cache:com/typesafe/akka/akka-cluster_2.10.0-M7/2.1-SNAPSHOT/akka-cluster_2.10.0-M7-2.1-SNAPSHOT.jar

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.1-SNAPSHOT" cross CrossVersion.full

libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.1-SNAPSHOT" cross CrossVersion.full

//libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.1-SNAPSHOT" cross CrossVersion.full

libraryDependencies += "com.typesafe.akka" %% "akka-cluster-experimental" % "2.1-SNAPSHOT" cross CrossVersion.full

libraryDependencies += "com.typesafe.akka" %% "akka-remote-tests-experimental" % "2.1-SNAPSHOT" cross CrossVersion.full

libraryDependencies += "us.theatr" % "akka-quartz" % "0.1-SNAPSHOT" cross CrossVersion.full

//resolvers += "akka-throttler-github-repository" at "http://hbf.github.com/akka-throttler/maven-repo"

//libraryDependencies += "akka.pattern.throttle" % "akka-throttler_2.10" % "1.0-SNAPSHOT"

