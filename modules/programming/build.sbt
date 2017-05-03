name := "programming"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")


libraryDependencies ++= Seq(
  // Test Jython
  "org.python" % "jython-standalone" % "2.7.1b3"
)

// JaCoCo - siehe project/plugins.sbt
jacoco.settings