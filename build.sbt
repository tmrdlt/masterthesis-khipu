addCompilerPlugin("org.wartremover" %% "wartremover" % "2.4.13" cross CrossVersion.full)
wartremoverErrors in(Compile, compile) += Wart.OptionPartial
wartremoverErrors in(Compile, compile) += Wart.TraversableOps

lazy val commonSettings = Seq(
  name := "masterthesis-khipu",
  organization := "de.tmrdlt",
  version := "0.1",
  scalaVersion := "2.13.5",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8", "-Xfatal-warnings")
)

mainClass := Some("de.tmrdlt.Main")

libraryDependencies ++= {
  // Dependencies
  val akkaVersion = "2.6.13"
  val akkaHttpVersion = "10.2.4"
  val slickVersion = "3.3.3"
  val slickPgVersion = "0.19.5"
  val postgresVersion = "42.2.19"
  val logbackVersion = "1.2.3"
  val mongoDbVersion = "4.2.2"

  // Test dependencies
  val scalaCheckVersion = "1.15.3"
  val scalaTestVersion = "3.2.6"
  val scalaMockVersion = "5.1.0"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
    "com.github.tminglei" %% "slick-pg" % slickPgVersion,
    "com.github.tminglei" %% "slick-pg_spray-json" % slickPgVersion,

    "org.postgresql" % "postgresql" % postgresVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "org.mongodb.scala" %% "mongo-scala-driver" % mongoDbVersion,

    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.scalamock" %% "scalamock" % scalaMockVersion % "test"
  )
}

lazy val root =
  (project in file("."))
    .settings(commonSettings: _*)
    .withId("khipu-api")

// The almond Docker container has to use a different DB url to access the DB container so for sbt assembly the config
// is changed
assemblyMergeStrategy in assembly := {
  case PathList("application.conf") => MergeStrategy.discard
  case PathList("almond.conf") => new MyMergeStrategy()
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
