name := "web"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.html.*", "model.javascript.*", "model.css.*")

libraryDependencies ++= Seq(
  javaJdbc,

  // Selenium for Html Tests
  Common.seleniumDep,

  // Mockito for Testing
  Common.mockitoDep
)
