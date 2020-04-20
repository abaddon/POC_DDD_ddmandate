name := "DDMandate"

version := "0.1"

scalaVersion := "2.13.1"

organization := "com.abaddon83"

libraryDependencies ++= {
  val akkaVersion = "2.6.4"
  val akkaHttp = "10.1.11"
  Seq(
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core"  % akkaHttp,
    "com.typesafe.akka" %% "akka-http"       % akkaHttp,
   // "com.typesafe.play" %% "play-ws-standalone-json"       % "1.1.8",
    "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.2.3",
    //"de.heikoseeberger" %% "akka-http-play-json"   % "1.17.0",
    "com.typesafe.akka" %% "akka-testkit"    % akkaVersion   % "test",
    "org.scalatest" %% "scalatest" % "3.1.1" % "test"
  )
}
