addCompilerPlugin("org.wartremover" %% "wartremover" % "2.4.16" cross CrossVersion.full)
Compile / compile / wartremoverErrors += Wart.OptionPartial
//Compile / compile / wartremoverErrors += Wart.TraversableOps

lazy val commonSettings = Seq(
  name := "masterthesis-khipu",
  organization := "de.tmrdlt",
  version := "0.1",
  scalaVersion := "2.13.6",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8", "-Xfatal-warnings")
)

mainClass := Some("de.tmrdlt.Main")

libraryDependencies ++= {
  val logbackVersion = "1.2.6"
  val slickPgVersion = "0.19.7"
  val scalaCsvVersion = "1.3.8"
  val akkaVersion = "2.6.16"
  val akkaHttpVersion = "10.2.6"
  val slickVersion = "3.3.3"
  val mongoDbVersion = "4.3.3"
  val optaplannerVersion = "8.11.1.Final"
  val postgresVersion = "42.2.24"
  val scalaCheckVersion = "1.15.4"
  val scalaMockVersion = "5.1.0"
  val scalaTestVersion = "3.2.10"

  Seq(
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "com.github.tminglei" %% "slick-pg" % slickPgVersion,
    "com.github.tminglei" %% "slick-pg_spray-json" % slickPgVersion,
    "com.github.tototoshi" %% "scala-csv" % scalaCsvVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
    "org.mongodb.scala" %% "mongo-scala-driver" % mongoDbVersion,
    "org.optaplanner" % "optaplanner-core" % optaplannerVersion,
    "org.postgresql" % "postgresql" % postgresVersion,
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test",
    "org.scalamock" %% "scalamock" % scalaMockVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
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
