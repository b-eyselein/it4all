name := "web"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*", "model.html.*", "model.javascript.*")

libraryDependencies ++= Seq(
  javaJdbc,

  // Selenium for Html Tests
  Common.seleniumDep,
  Common.htmlUnitDep
)

// JaCoCo - siehe project/plugins.sbt
jacoco.settings

