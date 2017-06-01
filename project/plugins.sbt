// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.15")

// Web plugins
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "3.1.0")

// JaCoCo - Code Coverage
addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.3.0")

// Upgrade javaassist to use certain Java 8 features (IntStream.range(), ...)
libraryDependencies += "org.javassist" % "javassist" % "3.21.0-GA"