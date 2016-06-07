name := "binary"

Common.settings

scalacOptions ++= Seq("-Xmax-classfile-name", "100")

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  javaJdbc,

  // Selenium for Html Tests
  Common.seleniumDep,

  // Mockito for Testing
  Common.mockitoDep
)
