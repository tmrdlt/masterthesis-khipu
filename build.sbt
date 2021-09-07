addCompilerPlugin("org.wartremover" %% "wartremover" % "2.4.13" cross CrossVersion.full)
Compile / compile / wartremoverErrors += Wart.OptionPartial
// Compile / compile / wartremoverErrors += Wart.TraversableOps TODO maybe enable later on

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
  val akkaVersion = "2.6.14"
  val akkaHttpVersion = "10.2.4"
  val slickVersion = "3.3.3"
  val slickPgVersion = "0.19.6"
  val mongoDbVersion = "4.2.3"
  val scalaCsvVersion = "1.3.7"

  val postgresVersion = "42.2.20"
  val logbackVersion = "1.2.3"

  // Test dependencies
  val scalaCheckVersion = "1.15.4"
  val scalaTestVersion = "3.2.8"
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
    "org.mongodb.scala" %% "mongo-scala-driver" % mongoDbVersion,
    "com.github.tototoshi" %% "scala-csv" % scalaCsvVersion,

    "org.postgresql" % "postgresql" % postgresVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "org.optaplanner" % "optaplanner-core" % "8.10.0.Final",

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
// https://stackoverflow.com/questions/36058600/rename-file-using-sbt-assembly
assembly / assemblyMergeStrategy := {
  case PathList("application.conf") => MergeStrategy.discard
  case PathList("almond.conf") => new MyMergeStrategy()
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}
