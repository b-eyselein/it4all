name := "core"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

//playEbeanModels in Compile := Seq("model.user.*", "model.exercise.*", "model.programming.*")

libraryDependencies ++= Seq(
  Common.jsonSchemaValidator,
  Common.jsonSchemaGenerator,

  "org.webjars" % "bootstrap" % "3.3.7-1"
  //  ,"org.webjars" % "Semantic-UI" % "2.2.10"
  //  ,"org.webjars" % "octicons" % "4.3.0"
)
