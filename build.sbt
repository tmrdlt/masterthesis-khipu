addCompilerPlugin("org.wartremover" %% "wartremover" % "2.4.13" cross CrossVersion.full)
wartremoverErrors in (Compile, compile) += Wart.OptionPartial
wartremoverErrors in (Compile, compile) += Wart.TraversableOps

lazy val commonSettings = Seq(
  name := "masterthesis-khipu",
  organization := "de.timoerdelt",
  version := "VERSIONTAG",
  scalaVersion := "2.13.4",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8", "-Xfatal-warnings")
)

mainClass := Some("de.timoerdelt.Main")

libraryDependencies ++= {
  // Dependencies
  val akkaVersion           = "2.6.10"
  val akkaHttpVersion       = "10.2.1"
  val slickVersion          = "3.3.3"
  val slickPgVersion        = "0.19.4"
  val jwtVersion            = "4.3.0"

  val postgresVersion       = "42.2.18"
  val logbackVersion        = "1.2.3"
  val uuidGeneratorVersion  = "4.0.1"

  // Test dependencies
  val scalaCheckVersion = "1.15.1"
  val scalaTestVersion  = "3.2.3"
  val scalaMockVersion  = "5.0.0"


  Seq(
    "com.typesafe.akka"   %% "akka-actor"           % akkaVersion,
    "com.typesafe.akka"   %% "akka-stream"          % akkaVersion,
    "com.typesafe.akka"   %% "akka-slf4j"           % akkaVersion,
    "com.typesafe.akka"   %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka"   %% "akka-http-core"       % akkaHttpVersion,
    "com.typesafe.akka"   %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.slick"  %% "slick"                % slickVersion,
    "com.typesafe.slick"  %% "slick-hikaricp"       % slickVersion,
    "com.github.tminglei" %% "slick-pg"             % slickPgVersion,
    "com.github.tminglei" %% "slick-pg_spray-json"  % slickPgVersion,
    "com.pauldijou"       %% "jwt-core"             % jwtVersion,

    "org.postgresql"        % "postgresql"            % postgresVersion,
    "ch.qos.logback"        % "logback-classic"       % logbackVersion,
    "com.fasterxml.uuid"    % "java-uuid-generator"   % uuidGeneratorVersion,

    "com.typesafe.akka"   %% "akka-testkit"                 % akkaVersion       % "it,test",
    "com.typesafe.akka"   %% "akka-http-testkit"            % akkaHttpVersion   % "it,test",
    "org.scalacheck"      %% "scalacheck"                   % scalaCheckVersion % "it,test",
    "org.scalatest"       %% "scalatest"                    % scalaTestVersion  % "it,test",
    "org.scalamock"       %% "scalamock"                    % scalaMockVersion  % "it,test"
  )
}

coverageMinimum := 80
coverageFailOnMinimum := false
coverageHighlighting := scalaBinaryVersion.value == "2.13"

lazy val IntegrationTest = config("it") extend Test

parallelExecution in Test := false

lazy val root =
  (project in file("."))
    .configs(IntegrationTest)
    .settings(commonSettings: _*)
    .settings(Defaults.itSettings: _*)
    .withId("workflow-api")


test in assembly := {}

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "commons", "logging", xs@_*) => MergeStrategy.first
  case "application.conf"                                     => MergeStrategy.concat
  case PathList("META-INF", xs@_*)                            => MergeStrategy.discard
  case PathList("OSGI-OPT", xs@_*)                            => MergeStrategy.discard
  case x => val baseStrategy = (assemblyMergeStrategy in assembly).value
            baseStrategy(x)
}

