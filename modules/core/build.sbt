name := "core"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.user.*", "model.exercise.*", "model.programming.*")

libraryDependencies ++= Seq(
  Common.jsonSchemaValidator,
  Common.jsonSchemaGenerator,

  //"org.webjars" % "jquery" % "3.2.1",
  "org.webjars" % "bootstrap" % "3.3.7-1"
)

EclipseKeys.classpathTransformerFactories := Seq(ClasspathentryTransformer)