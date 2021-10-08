logLevel := Level.Warn

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.4.16")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.3") // Usage: Terminal: "sbt dependencyUpdates"
