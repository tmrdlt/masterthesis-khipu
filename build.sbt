addCompilerPlugin("org.wartremover" %% "wartremover" % "2.4.13" cross CrossVersion.full)
wartremoverErrors in(Compile, compile) += Wart.OptionPartial
wartremoverErrors in(Compile, compile) += Wart.TraversableOps

lazy val root = Seq(
  name := "masterthesis-khipu",
  organization := "de.timoerdelt",
  scalaVersion := "2.12.13",
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
  val sparkVersion = "3.1.1"

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
    "org.apache.spark" %% "spark-core" % sparkVersion
      // Prevents warning "SLF4J: Class path contains multiple SLF4J bindings."
      exclude("org.slf4j","*"),

    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "it,test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "it,test",
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "it,test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "it,test",
    "org.scalamock" %% "scalamock" % scalaMockVersion % "it,test"
  )
}
