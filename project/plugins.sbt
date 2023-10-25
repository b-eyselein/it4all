// Use scala-xml 2.x for compatibility with sbt 1.8.x
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.20")

// Sbt Updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.4")
