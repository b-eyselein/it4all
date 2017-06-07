// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0-RC2")

// Web plugins
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.2.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "4.0.0-M3")

// JaCoCo - Code Coverage
addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.3.0")

// Upgrade javaassist to use certain Java 8 features (IntStream.range(), ...)
libraryDependencies += "org.javassist" % "javassist" % "3.21.0-GA"
