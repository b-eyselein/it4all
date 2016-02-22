name := "web"

playEbeanModels in Compile := Seq("model.html.*", "model.javascript.*")

libraryDependencies ++= Seq(
  javaJdbc,
  
  // Selenium for Html Tests
  "org.seleniumhq.selenium" % "selenium-java" % "2.48.1",
  
  // Mockito for Testing
  "org.mockito" % "mockito-core" % "1.9.5"
)

routesGenerator := InjectedRoutesGenerator