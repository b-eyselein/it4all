name := "xml"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

libraryDependencies ++= Seq(
  javaJdbc,

  // Selenium for Html Tests
  Common.seleniumDep,

  // Mockito for Testing
  Common.mockitoDep
)
