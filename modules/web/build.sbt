name := "web"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.html.*", "model.javascript.*")

libraryDependencies ++= Seq(
  javaJdbc,

  // Selenium for Html Tests
  "org.seleniumhq.selenium" % "selenium-java" % "2.52.0",

  // Mockito for Testing
  "org.mockito" % "mockito-core" % "1.10.19"
)
