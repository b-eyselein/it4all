// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.3")

// Wart remover for scalac
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.4.3")

// Sbt Updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.4.2")

// Typescript Interfaces Plugin
resolvers += Resolver.sonatypeRepo("snapshots")

addSbtPlugin("nl.codestar" % "sbt-scala-tsi" % "0.2.0-SNAPSHOT")
