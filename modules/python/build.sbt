name := "python"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

// JaCoCo - siehe project/plugins.sbt
jacoco.settings

libraryDependencies ++= Seq(
  javaJdbc,

  // Test Jython
  "org.python" % "jython-standalone" % "2.7.0"
)
  


