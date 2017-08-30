// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.3")

// Web plugins
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.2.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "4.0.2")

// JaCoCo - Java Code Coverage
addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.3.0")

// SCoverage for Scala
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.8")


// Upgrade javaassist to use certain Java 8 features (IntStream.range(), ...)
libraryDependencies += "org.javassist" % "javassist" % "3.22.0-CR2"
