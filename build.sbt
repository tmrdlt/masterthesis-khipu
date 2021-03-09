addCompilerPlugin("org.wartremover" %% "wartremover" % "2.4.13" cross CrossVersion.full)
wartremoverErrors in(Compile, compile) += Wart.OptionPartial
wartremoverErrors in(Compile, compile) += Wart.TraversableOps

lazy val commonSettings = Seq(
  name := "masterthesis-khipu",
  organization := "de.timoerdelt",
  version := "0.1",
  scalaVersion := "2.13.5",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8", "-Xfatal-warnings")
)

mainClass := Some("de.timoerdelt.Main")

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
  val scalaCheckVersion = "1.15.1"
  val scalaTestVersion = "3.2.3"
  val scalaMockVersion = "5.0.0"

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

    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "it,test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "it,test",
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "it,test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "it,test",
    "org.scalamock" %% "scalamock" % scalaMockVersion % "it,test"
  )
}

lazy val IntegrationTest = config("it") extend Test

lazy val root =
  (project in file("."))
    .configs(IntegrationTest)
    .settings(commonSettings: _*)
    .settings(Defaults.itSettings: _*)
    .withId("workflow-api")