name := "web"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*", "model.html.*", "model.javascript.*")

// JaCoCo - siehe project/plugins.sbt
jacoco.settings

libraryDependencies ++= Seq(
  javaJdbc,

  // Selenium for Html Tests
  Common.seleniumDep,
  Common.htmlUnitDep,

  // Mockito for Testing
  Common.mockitoDep
)
